package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;

@RequiredArgsConstructor
@Slf4j
public class BackendServiceBlockchainTransactionSubmissionService implements BlockchainTransactionSubmissionService {

    private final BackendService backendService;

    @Override
    @SneakyThrows
    public String submitTransaction(byte[] txData) {
        var result = backendService.getTransactionService().submitTransaction(txData);

        if (result.isSuccessful()) {
            return result.getValue();
        }

        throw new RuntimeException("Transaction submission failed with error: " + result.getResponse());
    }

}
