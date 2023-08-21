package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import org.cardano.foundation.voting.service.blockchain_state.BackendServiceBlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.blockchain_state.cardano_submit_api.CardanoSubmitApiBlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.http.HttpClient;

@Configuration
public class BlockchainDataConfig {

    @Bean
    @Profile("dev--yaci-dev-kit")
    public BlockchainTransactionSubmissionService backendServiceTransactionSubmissionService(BackendService backendService) {
        return new BackendServiceBlockchainTransactionSubmissionService(backendService);
    }

    @Bean
    @Profile( value = "dev--preprod" )
    public BlockchainTransactionSubmissionService noopCardanoSummitTransactionSubmissionService() {
        return new BlockchainTransactionSubmissionService.Noop();
    }

    @Bean
    @Profile( value = "prod" )
    public BlockchainTransactionSubmissionService cardanoSummitTransactionSubmissionService(HttpClient httpClient,
                                                                                            @Value("${cardano.tx.submit.api.url}") String cardanoSubmitApiUrl) {
        return new CardanoSubmitApiBlockchainTransactionSubmissionService(cardanoSubmitApiUrl, httpClient);
    }

}
