package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.extern.slf4j.Slf4j;

public interface BlockchainTransactionSubmissionService {

    /**
     * Submit transaction and return transaction  hash.
     *
     * @param txData
     * @return transaction hash
     */
    String submitTransaction(byte[] txData);

    @Slf4j
    class Noop implements BlockchainTransactionSubmissionService {

        @Override
        public String submitTransaction(byte[] txData) {
            String txHash = TransactionUtil.getTxHash(txData);

            log.info("Submitting transaction with hash: {}", txHash);

            return txHash;
        }

    }

}
