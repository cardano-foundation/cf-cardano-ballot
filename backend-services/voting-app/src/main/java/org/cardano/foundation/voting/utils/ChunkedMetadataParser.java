package org.cardano.foundation.voting.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Either;
import org.zalando.problem.Problem;

public class ChunkedMetadataParser {

    public static Either<Problem, String> parseArrayStringMetadata(JsonNode value) {
        if (value == null) {
            return Either.left(
                    Problem.builder()
                            .withTitle("NULL_VALUE")
                            .withDetail("Null value!")
                            .build()
            );
        }

        if (value.isArray()) {
            var sb = new StringBuilder();

            for (var it = value.elements(); it.hasNext();) {
                var element = it.next();
                sb.append(element.asText());
            }

            return Either.right(sb.toString());
        }

        return Either.left(
                Problem.builder()
                        .withTitle("NOT_ARRAY")
                        .withDetail("Not array!")
                        .build()
        );
    }

}
