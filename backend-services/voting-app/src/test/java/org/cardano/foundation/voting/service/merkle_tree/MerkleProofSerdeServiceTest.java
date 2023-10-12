package org.cardano.foundation.voting.service.merkle_tree;

import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cardanofoundation.merkle.ProofItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MerkleProofSerdeServiceTest {

    @Test
    public void deserialise01() {
        MerkleProofSerdeService merkleProofSerdeService = new MerkleProofSerdeService(new ObjectMapper());

        var json = "{\"steps\":[{\"R\":\"8e8a6cb359bb83f141498d96a80d7a9ce4c5558c115660820e0f2ac13555d934\"},{\"R\":\"0e8bff5e9692bfd3ea08131384678d5bce2cf680e4dddfb488f4407b92ef7327\"}]}";

        var items = merkleProofSerdeService.deserialise(json);

        assertEquals(2, items.size());

        assertTrue(items.get(0) instanceof ProofItem.Right);
        assertTrue(items.get(1) instanceof ProofItem.Right);

        assertEquals("8e8a6cb359bb83f141498d96a80d7a9ce4c5558c115660820e0f2ac13555d934", HexUtil.encodeHexString(items.get(0).hash()));
        assertEquals("0e8bff5e9692bfd3ea08131384678d5bce2cf680e4dddfb488f4407b92ef7327", HexUtil.encodeHexString(items.get(1).hash()));
    }

}
