package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.yaci.store.api.blocks.service.BlockService;
import com.bloxbean.cardano.yaci.store.api.transaction.service.TransactionService;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.FixedBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.BackendServiceBlockchainDataCurrentStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.backend_bridge.BackendServiceBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.yaci.YaciChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.yaci.YaciTransactionDetailsBlockchainDataService;
import org.cardano.foundation.voting.service.chain_sync.ChainSyncService;
import org.cardano.foundation.voting.service.chain_sync.DefaultChainSyncService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService(ChainNetwork network,
                                                                       BlockService blockService,
                                                                       ChainSyncService chainSyncService,
                                                                       CacheManager cacheManager) {
        return new YaciChainTipService(blockService, chainSyncService, network, cacheManager);
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
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService(ChainNetwork network,
                                                                                           BlockService blockService,
                                                                                           TransactionService transactionService,
                                                                                           CacheManager cacheManager
                                                                                           ) {
        return new YaciTransactionDetailsBlockchainDataService(blockService, transactionService, network, cacheManager);
    }

}
