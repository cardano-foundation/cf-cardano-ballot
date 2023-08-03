package org.cardano.foundation.voting.service.transaction_submit;

import org.cardano.foundation.voting.domain.L1SubmissionData;

import java.util.concurrent.TimeoutException;

public interface TransactionSubmissionService {

    /**
     * Submit transaction and return transaction  hash.
     *
     * @param txData
     * @return transaction hash
     */
    String submitTransaction(byte[] txData);

    /**
     * Submits transaction and gets L1 confirmation data
     * @param txData
     * @return
     */
    L1SubmissionData submitTransactionWithConfirmation(byte[] txData) throws TimeoutException;

}
