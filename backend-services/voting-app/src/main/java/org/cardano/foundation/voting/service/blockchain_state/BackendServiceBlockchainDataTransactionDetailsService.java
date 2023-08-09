package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.model.Block;
import com.bloxbean.cardano.client.backend.model.TransactionContent;
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
        var trxResult = backendService.getTransactionService().getTransaction(transactionHash);

        if (trxResult.isSuccessful()) {
            var trx = trxResult.getValue();

            var latestBlockResult = backendService.getBlockService().getLatestBlock();
            var txConfirmations = getTransactionConfirmations(latestBlockResult, trx).orElse(0L);

            return Optional.of(TransactionDetails.builder()
                    .transactionHash(trx.getHash())
                    .transactionsConfirmations(txConfirmations)
                    .finalityScore(TransactionDetails.FinalityScore.fromConfirmations(txConfirmations))
                    .absoluteSlot(trx.getSlot())
                    .blockHash(trx.getBlock())
                    .build()
            );
        }

        var code = trxResult.code();
        if (code == 404) {
            return Optional.empty();
        }

        throw new RuntimeException("Unable to get transaction details via backendService, reason: " + trxResult.getResponse());
    }

    private Optional<Long> getTransactionConfirmations(Result<Block> latestBlockResult, TransactionContent transactionContent) {
        if (latestBlockResult.isSuccessful()) {
            var latestBlock = latestBlockResult.getValue();

            return Optional.of(latestBlock.getSlot() - transactionContent.getSlot());
        }

        return Optional.empty();
    }

}
