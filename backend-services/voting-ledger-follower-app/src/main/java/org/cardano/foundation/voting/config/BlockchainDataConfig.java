package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.blockchain_state.*;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.*;
import org.cardano.foundation.voting.service.chain_sync.ChainSyncService;
import org.cardano.foundation.voting.service.chain_sync.DefaultChainSyncService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService(CardanoNetwork network,
                                                                       @Qualifier("yaci_blockfrost") BackendService backendService,
                                                                       ChainSyncService chainSyncService) {
        return new BackendServiceBlockchainDataChainTipService(backendService, chainSyncService, network);
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    @ConditionalOnProperty(prefix = "cardano.snapshot.bounds.check", value = "enabled", havingValue = "true")
    public BlockchainDataStakePoolService blockchainDataStakePoolService(@Qualifier("original_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataStakePoolService(backendService);
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    @ConditionalOnProperty(prefix = "cardano.snapshot.bounds.check", value = "enabled", havingValue = "false")
    public BlockchainDataStakePoolService blockchainDataCurrentStakePoolService(@Qualifier("original_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataCurrentStakePoolService(backendService);
    }

    @Bean
    @Profile( value = "dev--yaci-dev-kit" )
    public BlockchainDataStakePoolService dummyBlockchainDataStakePoolService() {
        return new FixedBlockchainDataStakePoolService(1000);
    }

    @Bean
    @Profile( value = "dev--yaci-dev-kit" )
    public ChainSyncService dummyChainSyncService() {
        return new ChainSyncService.Noop();
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    public ChainSyncService defaultChainSyncService(@Qualifier("yaci_blockfrost") BackendService yaciBackendService,
                                                    @Qualifier("original_blockfrost") BackendService orgBackendService,
                                                    @Value("${chain.sync.buffer:30}") int chainSyncBuffer) {
        return new DefaultChainSyncService(orgBackendService, yaciBackendService, chainSyncBuffer);
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService(CardanoNetwork network,
                                                                                           @Qualifier("yaci_blockfrost") BackendService backendService) {
        return new BackendServiceBlockchainDataTransactionDetailsService(backendService, network);
    }

}
