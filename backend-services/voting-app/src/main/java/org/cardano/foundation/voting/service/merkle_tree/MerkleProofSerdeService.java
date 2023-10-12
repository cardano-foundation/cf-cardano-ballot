package org.cardano.foundation.voting.service.merkle_tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerkleProofSerdeService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<ProofItem> deserialise(String proofItemJson) {
        var root = objectMapper.readTree(proofItemJson);

        var steps = root.get("steps");

        if (!steps.isArray()) {
            return List.of();
        }

        ArrayList<ProofItem> items = new ArrayList<>();
        for (Iterator<JsonNode> it = steps.iterator(); it.hasNext();) {
            var item = it.next();
            items.add(deserialiseProofItem(item));
        }

        return items;
    }

    private ProofItem deserialiseProofItem(JsonNode item) {
        if (item.has("L")) {
            return new ProofItem.Left(decodeHexString(item.get("L").asText()));
        }
        if (item.has("R")) {
            return new ProofItem.Right(decodeHexString(item.get("R").asText()));
        }

        throw new IllegalArgumentException("Invalid proof item: " + item);
    }

}
