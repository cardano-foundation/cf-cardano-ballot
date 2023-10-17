package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.transaction.spec.Transaction;

public interface TransactionSubmissionService {

    Result<String> submitTransaction(Transaction transactions) throws CborSerializationException;

    Result<String> submitTransaction(String cborHex);

}
