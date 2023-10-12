package org.cardano.foundation.voting.service.merkle_tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;

@Service
@RequiredArgsConstructor
public class MerkleProofSerdeService {

    private final ObjectMapper objectMapper;

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

}
