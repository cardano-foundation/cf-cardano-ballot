package org.cardano.foundation.voting.service.transaction_submit;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @Autowired
    private L1TransactionCreator l1TransactionCreator;

    public String submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        byte[] txData = l1TransactionCreator.submitMerkleCommitments(l1MerkleCommitments);

        return transactionSubmissionService.submitTransaction(txData);
    }

}
