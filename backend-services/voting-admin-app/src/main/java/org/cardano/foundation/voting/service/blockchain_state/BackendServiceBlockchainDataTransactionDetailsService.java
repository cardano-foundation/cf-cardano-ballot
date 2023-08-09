package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TransactionDetails;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BackendServiceBlockchainDataTransactionDetailsService implements BlockchainDataTransactionDetailsService {

    private final BackendService backendService;

    @Override
    @SneakyThrows
    public Optional<TransactionDetails> getTransactionDetails(String transactionHash) {
        var result = backendService.getTransactionService().getTransaction(transactionHash);

        if (result.isSuccessful()) {
            var trx = result.getValue();

            return Optional.of(TransactionDetails.builder()

                    .transactionHash(trx.getHash())
                    .absoluteSlot(trx.getSlot())
                    .blockHash(trx.getBlock())
                    .build()
            );
        }

        throw new RuntimeException("Unable to get transaction details via backendService, reason: " + result.getResponse());
    }

}
