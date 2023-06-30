package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import lombok.SneakyThrows;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;

public class BlockfrostTransactionSubmissionService extends AbstractBlockfrostService implements BlockchainTransactionSubmissionService {

    @Override
    @SneakyThrows
    public String submitTransaction(byte[] txData) {
        return transactionService.submitTransaction(txData);
    }

}
