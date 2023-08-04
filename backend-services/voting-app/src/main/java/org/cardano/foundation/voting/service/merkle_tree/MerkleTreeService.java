package org.cardano.foundation.voting.service.merkle_tree;

import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.L1MerkleTree;
import org.cardano.foundation.voting.repository.MerkleTreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;

@Service
public class MerkleTreeService {

    @Autowired
    private MerkleTreeRepository merkleTreeRepository;

    public void storeAll(Iterable<L1MerkleCommitment> l1MerkleCommitments, String l1TransactionHash, long l1TransactionSlot) {
        for (L1MerkleCommitment l1MerkleCommitment : l1MerkleCommitments) {
            var merkleRoot = l1MerkleCommitment.root();
            var merkleRootHash = encodeHexString(l1MerkleCommitment.root().itemHash());
            var l1MerkleTree = L1MerkleTree.builder()
                    .transactionHash(l1TransactionHash)
                    .rootHash(merkleRootHash)
                    .absoluteSlot(l1TransactionSlot)
                    .root(merkleRoot)
                    .build();

            merkleTreeRepository.storeForEvent(l1MerkleCommitment.event(), l1MerkleTree);
        }
    }

}
