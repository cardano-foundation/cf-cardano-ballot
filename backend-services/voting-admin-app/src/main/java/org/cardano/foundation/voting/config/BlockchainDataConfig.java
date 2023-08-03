package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataTipService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainTransactionSubmissionService transactionSubmissionService() {
        return new BlockchainTransactionSubmissionService.Noop();
        //return new CardanoSubmitApiBlockchainTransactionSubmissionService();
    }

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService() {
        return new BlockfrostBlockchainDataTipService();
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService() {
        return new BlockchainDataTransactionDetailsService() { };
    }

}
