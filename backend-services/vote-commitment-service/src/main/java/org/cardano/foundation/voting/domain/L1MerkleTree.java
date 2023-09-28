package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardanofoundation.merkle.MerkleElement;

/**
 * Merkle Tree that has a root committed to Cardano's L1
 */
@AllArgsConstructor
@Getter
@Builder
public class L1MerkleTree {

    private MerkleElement<Vote> root;
    private String rootHash;
    private String transactionHash;
    private long absoluteSlot;

}
