package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;

public interface TransactionProcessor {

    Result<String> submitTransaction(byte[] var1) throws ApiException;

}
