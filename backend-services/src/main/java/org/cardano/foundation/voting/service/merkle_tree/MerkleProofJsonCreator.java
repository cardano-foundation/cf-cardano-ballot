package org.cardano.foundation.voting.service.merkle_tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.MerkleProof;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;

@Service
public class MerkleProofJsonCreator {

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode serialise(MerkleProof merkleProof) {
        var root = objectMapper.createObjectNode();
        root.put("rootHash", merkleProof.getRootHash());

        // this is not a mistake an empty proof items is also a valid proof pointing to the root of the tree
        if (merkleProof.getProofItems().isEmpty()) {
            root.putNull("proofItems");

            return root;
        }

        var data = root.putObject("proofItems");

        for (var proofItem : merkleProof.getProofItems().get()) {
            if (proofItem instanceof ProofItem.Left pl) {
                data.put("left", encodeHexString(pl.getHash()));
            }
            if (proofItem instanceof ProofItem.Right pr) {
                data.put("right", encodeHexString(pr.getHash()));
            }
        }

        return root;
    }

    @SneakyThrows
    public String serialiseAsString(MerkleProof merkleProof) {
        var root = serialise(merkleProof);

        return objectMapper.writeValueAsString(root);
    }

}
