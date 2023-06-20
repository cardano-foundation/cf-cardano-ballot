package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.cardanofoundation.merkle.ProofItem;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@ToString
public class MerkleProof {

    private Optional<List<ProofItem>> proofItems;
    private String rootHash; // in hex

}
