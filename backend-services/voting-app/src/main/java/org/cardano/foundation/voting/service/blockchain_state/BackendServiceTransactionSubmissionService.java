package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackendServiceTransactionSubmissionService implements BlockchainTransactionSubmissionService {

    private final BackendService backendService;

    @SneakyThrows
    @Override
    public String submitTransaction(byte[] txData) {
        var result = backendService.getTransactionService().submitTransaction(txData);

        if (result.isSuccessful()) {
            return result.getValue();
        }

        throw new RuntimeException("Transaction submission failed with error: " + result.getResponse());
    }

}
