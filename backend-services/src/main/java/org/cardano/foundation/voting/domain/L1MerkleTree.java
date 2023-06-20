package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardanofoundation.merkle.MerkleElement;

/**
 * Merkle Tree that has a root committed to Cardano's L1
 */
@AllArgsConstructor
@Getter
public class L1MerkleTree {

    private MerkleElement<Vote> root;
    private String rootHash;
    private String transactionHash;

}
