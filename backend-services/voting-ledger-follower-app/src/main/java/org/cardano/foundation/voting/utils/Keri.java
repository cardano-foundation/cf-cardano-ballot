package org.cardano.foundation.voting.utils;

import io.vavr.control.Either;
import lombok.val;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

public final class Keri {

    public static Either<Problem, Boolean> checkAid(String aid) {
        if (aid == null || aid.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_KERI_AID")
                    .withDetail("Aid is required")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        if (aid.length() != 44) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_KERI_AID")
                    .withDetail("Aid must be 44 characters long")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val firstChar = aid.charAt(0);

        if (firstChar != 'E' && firstChar != 'B') {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_KERI_AID")
                    .withDetail("Aid must start with 'E' or 'B'")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        return Either.right(true);
    }

}
