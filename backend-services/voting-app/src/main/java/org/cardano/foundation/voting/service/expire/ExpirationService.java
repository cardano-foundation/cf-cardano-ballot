package org.cardano.foundation.voting.service.expire;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class ExpirationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Value("${expiration.slot.buffer}")
    private long expirationSlotBuffer;

    public Either<Problem, Boolean> isSlotInRange(long slot) {
        var chainTipE = chainFollowerClient.getChainTip();
        if (chainTipE.isEmpty()) {
            log.warn("Slot maybe NOT expired but we have no way to check this since we have no chain tip access.");

            return Either.left(Problem.builder()
                    .withTitle("CHAIN_TIP_ERROR")
                    .withDetail("Unable to get chain tip from chain-tip follower service, reason: chain tip not available")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        var chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.absoluteSlot();

        var range = Range
                .from(Range.Bound.inclusive(currentAbsoluteSlot - expirationSlotBuffer))
                .to(Range.Bound.inclusive(currentAbsoluteSlot + expirationSlotBuffer));

        return Either.right(range.contains(slot));
    }

    public Either<Problem, Boolean> isSlotExpired(long slot) {
        return isSlotInRange(slot).map(isSlotInRange -> !isSlotInRange);
    }

}
