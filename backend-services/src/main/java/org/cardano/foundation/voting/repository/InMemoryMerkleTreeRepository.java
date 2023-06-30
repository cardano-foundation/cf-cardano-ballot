package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.L1MerkleTree;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMerkleTreeRepository implements MerkleTreeRepository {

    private final ConcurrentHashMap<String, L1MerkleTree> merkleTrees = new ConcurrentHashMap<>();

    @Override
    public Optional<L1MerkleTree> findByEvent(Event event) {
        return Optional.ofNullable(merkleTrees.get(event.getId()));
    }

    @Override
    public void storeForEvent(Event event, L1MerkleTree l1MerkleTree) {
        merkleTrees.put(event.getId(), l1MerkleTree);
    }

}
