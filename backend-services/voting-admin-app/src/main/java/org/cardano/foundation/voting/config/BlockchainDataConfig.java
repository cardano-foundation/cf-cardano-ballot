package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.*;
import org.cardano.foundation.voting.service.transaction_submit.BackendServiceBlockchainTransactionSubmissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.http.HttpClient;

@Configuration
@Slf4j
public class BlockchainDataConfig {

    @Bean
    @Profile("dev--yaci-dev-kit")
    public BlockchainTransactionSubmissionService backendServiceTransactionSubmissionService(BackendService backendService) {
        return new BackendServiceBlockchainTransactionSubmissionService(backendService);
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    public BlockchainTransactionSubmissionService cardanoSummitTransactionSubmissionService(HttpClient httpClient) {
        return new CardanoSubmitApiBlockchainTransactionSubmissionService(httpClient);
    }

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService(BackendService backendService) {
        return new BackendServiceBlockchainDataChainTipService(backendService);
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService(BackendService backendService) {
        return new BackendServiceBlockchainDataTransactionDetailsService(backendService);
    }

}

