package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.cardano.foundation.voting.utils.MoreBytes.humanReadableByteCountBin;

@Component
@Slf4j
@AllArgsConstructor
public class HydraTxSubmissionService implements TransactionSubmissionService {

    private final HydraReactiveClient hydraClient;

    @Override
    public Result<String> submitTransaction(byte[] txCbor) {
        log.info("Transaction size: {}", humanReadableByteCountBin(txCbor.length));

        val txHash = TransactionUtil.getTxHash(txCbor);

        val txResultMono = hydraClient.submitTxFullConfirmation(txHash, txCbor);
        val txResult = txResultMono.block(Duration.ofMinutes(1));

        return Result.create(txResult.isValid(), txResult.getMessage())
                .withValue(txResult.getTxId());
    }

}
