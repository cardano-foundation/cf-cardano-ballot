package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.model.Block;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;

public class BlockfrostBlockchainDataTipService extends AbstractBlockfrostService implements BlockchainDataChainTipService {

    @Override
    public ChainTip getChainTip() {
        var block = latestBlock();

        return ChainTip.builder()
                .absoluteSlot(block.getSlot())
                .build();
    }

    @SneakyThrows
    private Block latestBlock() {
        return blockService.getLatestBlock();
    }

}
