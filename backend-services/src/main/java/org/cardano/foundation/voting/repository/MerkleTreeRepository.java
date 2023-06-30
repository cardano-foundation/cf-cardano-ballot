package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.L1MerkleTree;
import org.cardano.foundation.voting.domain.entity.Event;

import java.util.Optional;

public interface MerkleTreeRepository {

    Optional<L1MerkleTree> findByEvent(Event event);

    void storeForEvent(Event event, L1MerkleTree merkleTree);

}
