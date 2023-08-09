package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainTip;

@Slf4j
@RequiredArgsConstructor
public class BackendServiceBlockchainDataChainTipService implements BlockchainDataChainTipService {

    private final BackendService backendService;

    @Override
    @SneakyThrows
    public ChainTip getChainTip() {
        var latestBlock = backendService.getBlockService().getLatestBlock();

        if (latestBlock.isSuccessful()) {
            var block = latestBlock.getValue();

            return ChainTip.builder()
                    .hash(block.getHash())
                    .absoluteSlot(block.getSlot())
                    .build();
        }

        throw new RuntimeException("Unable to get chain tip via backend service, reason:" + latestBlock.getResponse());
    }

}
