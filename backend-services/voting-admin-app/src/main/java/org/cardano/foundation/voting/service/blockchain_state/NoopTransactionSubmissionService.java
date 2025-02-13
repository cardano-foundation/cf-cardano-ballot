package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopTransactionSubmissionService implements BlockchainTransactionSubmissionService {

    @Override
    public String submitTransaction(byte[] txData) {
        String txHash = TransactionUtil.getTxHash(txData);

        log.info("Submitting transaction with hash: {}", txHash);

        return txHash;
    }

}
