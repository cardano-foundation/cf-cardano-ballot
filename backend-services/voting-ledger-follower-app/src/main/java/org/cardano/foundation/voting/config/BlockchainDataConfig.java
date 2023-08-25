package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.FixedBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.BackendServiceBlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.BackendServiceBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.BackendServiceBlockchainDataTransactionDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService(CardanoNetwork network,
                                                                       @Qualifier("yaci_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataChainTipService(backendService, network);
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    public BlockchainDataStakePoolService blockchainDataStakePoolService(@Qualifier("original_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataStakePoolService(backendService);
    }

    @Bean
    @Profile( value = "dev--yaci-dev-kit" )
    public BlockchainDataStakePoolService dummyBlockchainDataStakePoolService() {
        return new FixedBlockchainDataStakePoolService(1000);
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService(CardanoNetwork network,
                                                                                           @Qualifier("yaci_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataTransactionDetailsService(backendService, network);
    }

}
