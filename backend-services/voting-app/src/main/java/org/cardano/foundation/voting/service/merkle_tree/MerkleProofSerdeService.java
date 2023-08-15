package org.cardano.foundation.voting.service.merkle_tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;

@Service
public class MerkleProofSerdeService {

    @Autowired
    protected ObjectMapper objectMapper;

    public JsonNode serialise(List<ProofItem> proofItems) {
        var root = objectMapper.createObjectNode();

        // this is not a mistake an empty proof items is also a valid proof pointing to the root of the tree
        if (proofItems.isEmpty()) {
            root.putArray("steps");

            return root;
        }

        var array = root.putArray("steps");

        for (var proofItem : proofItems) {
            if (proofItem instanceof ProofItem.Left pl) {
                array.addObject()
                        .put("L", encodeHexString(pl.getHash()));
            }
            if (proofItem instanceof ProofItem.Right pr) {
                array.addObject()
                        .put("R", encodeHexString(pr.getHash()));
            }
        }

        return root;
    }

    @SneakyThrows
    public String serialiseAsString(List<ProofItem> proofItems) {
        var root = serialise(proofItems);

        return objectMapper.writeValueAsString(root);
    }

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
