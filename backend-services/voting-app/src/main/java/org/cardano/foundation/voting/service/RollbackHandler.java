package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RollbackHandler {

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Autowired
    private VoteMerkleProofRepository voteMerkleProofRepository;

//    @EventListener
//    public void onRollbackEvent(RollbackEvent event) {
//        // once we detect rollback event
//        // find all merkle proofs for the rollback event
//        // and delete them
//    }

}
