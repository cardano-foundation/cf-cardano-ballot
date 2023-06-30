package org.cardano.foundation.voting.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class ChunkedMetadataParser {

    public static String parseArrayStringMetadata(JsonNode value) {
        var sb = new StringBuilder();

        if (value.isArray()) {
            for (var it = value.elements(); it.hasNext();) {
                var element = it.next();
                sb.append(element.asText());
            }

            return sb.toString();
        }

        return value.asText();
    }

    public static String parseArrayStringMetadata(List<String> value) {
        return value.stream().reduce((a, b) -> a + b).orElse("");
    }

}
