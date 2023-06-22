package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataTipService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostTransactionSubmissionService;
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

}
