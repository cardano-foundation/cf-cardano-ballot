package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.blockchain_state.*;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.yaci.YaciBlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.yaci.YaciTransactionDetailsBlockchainDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService() {
        return new YaciBlockchainDataChainTipService();
    }

    @Bean
    public BlockchainDataMetadataService blockchainDataMetadataService() {
        return new BlockchainDataMetadataService() { };
    }

    @Bean
    public BlockchainDataStakePoolService blockchainDataStakePoolService() {
        return new BlockfrostBlockchainDataStakePoolService();
    }

    @Bean
    public BlockchainTransactionSubmissionService transactionSubmissionService() {
        return new BlockchainTransactionSubmissionService.Noop();
        //return new CardanoSubmitApiBlockchainTransactionSubmissionService();
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService() {
        return new YaciTransactionDetailsBlockchainDataService();
    }

}
