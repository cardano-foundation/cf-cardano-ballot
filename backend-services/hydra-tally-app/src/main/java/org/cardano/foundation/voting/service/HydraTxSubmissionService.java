package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;

import java.time.Duration;

import static org.cardano.foundation.voting.utils.MoreBytes.humanReadableByteCountBin;

@Slf4j
@AllArgsConstructor
public class HydraTxSubmissionService implements TransactionSubmissionService {

    private final HydraReactiveClient hydraClient;

    public Result<String> submitTransaction(Transaction transaction) throws CborSerializationException {
        val txCbor = transaction.serialize();
        log.info("Transaction size: {}, fee: {} lovelaces", humanReadableByteCountBin(txCbor.length), transaction.getBody().getFee());

        val txHash = TransactionUtil.getTxHash(txCbor);

        val txResultMono = hydraClient.submitTxFullConfirmation(txHash, txCbor);
        val txResult = txResultMono.block(Duration.ofMinutes(1));

        return Result.create(txResult.isValid(), txResult.getReason())
                .withValue(txResult.getTxId());
    }

    @Override
    public Result<String> submitTransaction(String cborHex) {
        throw new UnsupportedOperationException();
    }

}
