package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.blockchain_state.*;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService() {
        return new BlockfrostBlockchainDataTipService();
    }

    @Bean
    public BlockchainDataMetadataService blockchainDataMetadataService() {
        return new BlockfrostBlockchainDataMetadataService();
    }

    @Bean
    public BlockchainDataStakePoolService blockchainDataStakePoolService() {
        return new BlockfrostBlockchainDataStakePoolService();
    }

    @Bean
    public BlockchainTransactionSubmissionService transactionSubmissionService() {
        return new BlockfrostTransactionSubmissionService();
    }

    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService() {
        return new BlockfrostBlockchainDataTransactionDetailsService();
    }

}
