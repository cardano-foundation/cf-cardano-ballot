package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardanofoundation.hydra.cardano.client.lib.submit.TransactionSubmissionService;
import org.cardanofoundation.hydra.core.utils.HexUtils;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;

import java.time.Duration;

import static org.cardanofoundation.hydra.core.utils.MoreBytes.humanReadableByteCountBin;

@Slf4j
@AllArgsConstructor
public class ReactiveWebSocketHydraTxSubmissionService implements TransactionSubmissionService {

    private final HydraReactiveClient hydraClient;

    @Override
    @SneakyThrows
    public Result<String> submitTransaction(byte[] txData) {
        var transaction = Transaction.deserialize(txData);
        var fee = transaction.getBody().getFee();

        log.info("Transaction size: {}, fee: {} lovelaces", humanReadableByteCountBin(txData.length), fee);

        val txHash = TransactionUtil.getTxHash(transaction);

        val txResultMono = hydraClient.submitTxFullConfirmation(txHash, txData);
        val txResult = txResultMono.block(Duration.ofMinutes(1));

        return Result.create(txResult.isValid(), txResult.getReason())
                .withValue(txResult.getTxId());
    }

    public Result<String> submitTransaction(Transaction transaction) throws CborSerializationException {
        var fee = transaction.getBody().getFee();
        var txData = transaction.serialize();

        log.info("Transaction size: {}, fee: {} lovelaces", humanReadableByteCountBin(txData.length), fee);

        val txHash = TransactionUtil.getTxHash(transaction);

        val txResultMono = hydraClient.submitTxFullConfirmation(txHash, txData);
        val txResult = txResultMono.block(Duration.ofMinutes(1));

        return Result.create(txResult.isValid(), txResult.getReason())
                .withValue(txResult.getTxId());
    }

    @Override
    public Result<String> submitTransaction(String cborHex) {
        return submitTransaction(HexUtils.decodeHexString(cborHex));
    }

}
