package org.cardano.foundation.voting.domain.web3;

import io.vavr.control.Either;
import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.utils.Enums;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;

@Builder
@Getter
public class CIP93Envelope<T> {

    private String uri;
    private String action;
    private String actionText;
    private String slot;

    private T data;

    public Either<Problem, Long> getSlotAsLong() {
        try {
            return Either.right(Long.parseLong(slot));
        } catch (Exception e) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_SLOT")
                    .withDetail("Slot is not a valid number")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
    }

    public Optional<Web3Action> getActionAsEnum() {
        return Enums.getIfPresent(Web3Action.class, action);
    }

}
