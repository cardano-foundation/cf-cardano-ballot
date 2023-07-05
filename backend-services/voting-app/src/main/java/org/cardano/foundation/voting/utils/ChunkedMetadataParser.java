package org.cardano.foundation.voting.utils;

import co.nstant.in.cbor.model.UnicodeString;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataList;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.zalando.problem.Problem;

import java.util.Optional;

@Slf4j
public class ChunkedMetadataParser {

    public static Optional<String> deChunk(Object obj) {
        if (obj instanceof String s) {
            return Optional.of(s);
        }
        if (obj instanceof UnicodeString us) {
            return Optional.of(us.getString());
        }

        if (obj instanceof CBORMetadataList l) {
            var sb = new StringBuilder();

            for (int i = 0;  i < l.size(); i++) {
                var data = (String) l.getValueAt(i);
                sb.append(data);
            }

            return Optional.of(sb.toString());
        }

        return Optional.empty();
    }

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
