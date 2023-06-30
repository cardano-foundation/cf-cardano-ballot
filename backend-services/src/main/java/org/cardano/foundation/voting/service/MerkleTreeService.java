package org.cardano.foundation.voting.service;

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

    public void storeAll(Iterable<L1MerkleCommitment> l1MerkleCommitments, String l1TransactionHash) {
        for (L1MerkleCommitment l1MerkleCommitment : l1MerkleCommitments) {
            var merkleRoot = l1MerkleCommitment.root();
            var merkleRootHash = encodeHexString(l1MerkleCommitment.root().itemHash());
            var l1MerkleTree = new L1MerkleTree(merkleRoot, merkleRootHash, l1TransactionHash);

            merkleTreeRepository.storeForEvent(l1MerkleCommitment.event(), l1MerkleTree);
        }
    }

}
