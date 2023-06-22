package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.exception.APIException;
import io.blockfrost.sdk.api.model.Block;
import io.blockfrost.sdk.api.model.Transaction;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;

import java.util.Optional;

public class BlockfrostBlockchainDataTransactionDetailsService extends AbstractBlockfrostService implements BlockchainDataTransactionDetailsService {

    @SneakyThrows
    @Override
    public Optional<TransactionDetails> getTransactionDetails(String transactionHash) {
        return getTransaction(transactionHash).map(trx -> {
            var blockConfirmations = getBlockInSlot(trx).getConfirmations();

            return TransactionDetails.builder()
                    .transactionHash(transactionHash)
                    .absoluteSlot(trx.getSlot())
                    .blockHash(trx.getBlock())
                    .transactionsConfirmations(blockConfirmations)
                    .confirmationScore(TransactionDetails.ConfirmationScore.fromConfirmations(blockConfirmations))
                    .build();
        });
    }

    @SneakyThrows
    private Block getBlockInSlot(Transaction trx) {
        return blockService.getBlockInSlot(trx.getSlot());
    }

    private Optional<Transaction> getTransaction(String transactionHash) throws APIException {
        try {
            return Optional.of(transactionService.getTransaction(transactionHash));
        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return Optional.empty();
            } else {
                throw e;
            }
        }
    }
}
