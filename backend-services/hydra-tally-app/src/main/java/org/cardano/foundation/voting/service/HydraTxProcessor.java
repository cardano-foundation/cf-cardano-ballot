package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.cardanofoundation.hydra.reactor.TxResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.cardano.foundation.voting.utils.MoreBytes.humanReadableByteCountBin;

@Component
@Slf4j
@AllArgsConstructor
public class HydraTxProcessor implements TransactionProcessor {

    private final HydraReactiveClient hydraClient;

    @Override
    public Result<String> submitTransaction(byte[] txCbor) {
        log.info("Transaction size: {}", humanReadableByteCountBin(txCbor.length));

        Mono<TxResult> txResultM = hydraClient.submitTxFullConfirmation(txCbor);
        TxResult txResult = txResultM.block(Duration.ofMinutes(1));

        return Result.create(txResult.isValid(), txResult.getMessage())
                .withValue(txResult.getTxId());
    }

}
