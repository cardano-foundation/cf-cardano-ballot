package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataTipService;
import org.cardano.foundation.voting.service.blockchain_state.cardano_submit_api.CardanoSubmitApiBlockchainTransactionSubmissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockchainDataConfig {

    @Bean
    public BlockchainTransactionSubmissionService transactionSubmissionService() {
        return new CardanoSubmitApiBlockchainTransactionSubmissionService();
    }

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService() {
        return new BlockfrostBlockchainDataTipService();
    }

}
