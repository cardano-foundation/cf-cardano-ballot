package org.cardano.foundation.voting.service.transaction_submit;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @Autowired
    private L1TransactionCreator l1TransactionCreator;

    public String submitEvent(CreateEventCommand event) {
        byte[] txData = l1TransactionCreator.submitEvent(event);

        return transactionSubmissionService.submitTransaction(txData);
    }

    public String submitCategory(CreateCategoryCommand category) {
        byte[] txData = l1TransactionCreator.submitCategory(category);

        return transactionSubmissionService.submitTransaction(txData);
    }

}
