package org.cardano.foundation.voting.service.merkle_tree;

import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.Value;
import org.cardanofoundation.merkle.MerkleTree;
import org.cardanofoundation.merkle.ProofItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MerkleProofSerdeServiceTest {

    @Test
    public void serialise01() {
        MerkleProofSerdeService merkleProofSerdeService = new MerkleProofSerdeService();
        merkleProofSerdeService.objectMapper = new ObjectMapper();

        var root = MerkleTree.fromList(List.of("a", "b", "c", "d", "e", "f"), String::getBytes);

        var proof = MerkleTree.getProof(root, "a", String::getBytes)
                .map(Value::toJavaList);

        var json = merkleProofSerdeService.serialise(proof.orElseThrow());

        System.out.println(json);

        var steps = json.get("steps");

        assertEquals(2, steps.size());

        var it = steps.elements();
        var one = (ObjectNode) it.next();
        var two = (ObjectNode) it.next();

        assertEquals("Right", one.fields().next().getKey());
        assertEquals("8e8a6cb359bb83f141498d96a80d7a9ce4c5558c115660820e0f2ac13555d934", one.fields().next().getValue().asText());

        assertEquals("Right", two.fields().next().getKey());
        assertEquals("0e8bff5e9692bfd3ea08131384678d5bce2cf680e4dddfb488f4407b92ef7327", two.fields().next().getValue().asText());
    }

    @Test
    public void deserialise01() {
        MerkleProofSerdeService merkleProofSerdeService = new MerkleProofSerdeService();
        merkleProofSerdeService.objectMapper = new ObjectMapper();

        var json = "{\"steps\":[{\"Right\":\"8e8a6cb359bb83f141498d96a80d7a9ce4c5558c115660820e0f2ac13555d934\"},{\"Right\":\"0e8bff5e9692bfd3ea08131384678d5bce2cf680e4dddfb488f4407b92ef7327\"}]}";

        var items = merkleProofSerdeService.deserialise(json);

        assertEquals(2, items.size());

        assertTrue(items.get(0) instanceof ProofItem.Right);
        assertTrue(items.get(1) instanceof ProofItem.Right);

        assertEquals("8e8a6cb359bb83f141498d96a80d7a9ce4c5558c115660820e0f2ac13555d934", HexUtil.encodeHexString(items.get(0).hash()));
        assertEquals("0e8bff5e9692bfd3ea08131384678d5bce2cf680e4dddfb488f4407b92ef7327", HexUtil.encodeHexString(items.get(1).hash()));
    }

}
