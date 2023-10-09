package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TxResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.cardano.foundation.voting.utils.MoreBytes.humanReadableByteCountBin;

@Component
@Slf4j
@AllArgsConstructor
public class HydraTxProcessor implements TransactionProcessor {

    private final HydraTransactionClient hydraTransactionClient;

    @Override
    public Result<String> submitTransaction(byte[] txCbor) {
        log.info("Transaction size: {}", humanReadableByteCountBin(txCbor.length));

        String txHash = TransactionUtil.getTxHash(txCbor);

        Mono<TxResult> mono = hydraTransactionClient.submitTxFullConfirmation(txCbor);

        TxResult txResult = mono.block(Duration.ofMinutes(10));

        return Result.create(txResult.isValid(), txResult.getMessage())
                .withValue(txResult.getTxId());
    }

}
