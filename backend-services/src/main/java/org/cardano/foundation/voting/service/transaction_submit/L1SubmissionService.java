package org.cardano.foundation.voting.service.transaction_submit;

import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class L1SubmissionService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    public String submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        // create L1 metadata transaction
        // post metadata transaction via transactionSubmissionService

        // submit metadata transaction via blockfrost containing latest merkle root hash for this event
        // TODO - how do we make sure we are submitting, not somebody else???

        // TODO with yaci + CCL >= 0.5
        return null;
    }

    public String submitEvent(Event event) {
        // create L1 metadata transactions
        return null;
    }

}


