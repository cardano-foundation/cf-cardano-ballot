package org.cardano.foundation.voting.utils;

import co.nstant.in.cbor.model.UnicodeString;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataList;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

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

}
