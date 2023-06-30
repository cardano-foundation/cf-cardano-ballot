package org.cardano.foundation.voting.service.merkle_tree;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.Value;
import org.cardanofoundation.merkle.MerkleTree;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
class MerkleProofSerdeServiceTest {

    @Autowired
    private MerkleProofSerdeService merkleProofSerdeService;

    @Test
    public void serialise01() {
        var root = MerkleTree.fromList(List.of("a", "b", "c", "d", "e", "f"), String::getBytes);

        var proof = MerkleTree.getProof(root, "a", String::getBytes)
                .map(Value::toJavaList);

        var json = merkleProofSerdeService.serialise(proof.orElseThrow());

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

}
