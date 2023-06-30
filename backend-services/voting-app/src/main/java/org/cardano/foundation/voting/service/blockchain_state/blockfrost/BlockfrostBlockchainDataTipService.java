package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.model.Block;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;

public class BlockfrostBlockchainDataTipService extends AbstractBlockfrostService implements BlockchainDataChainTipService {

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Override
    public ChainTip getChainTip() {
        var block = latestBlock();

        return ChainTip.builder()
                .epochNo(block.getEpoch())
                .absoluteSlot(block.getSlot())
                .cardanoNetwork(cardanoNetwork)
                .build();
    }

    @SneakyThrows
    private Block latestBlock() {
        return blockService.getLatestBlock();
    }

}
