package org.cardano.foundation.voting.service.blockchain_state;

public interface BlockchainTransactionSubmissionService {

    String submitTransaction(byte[] txData);

}
