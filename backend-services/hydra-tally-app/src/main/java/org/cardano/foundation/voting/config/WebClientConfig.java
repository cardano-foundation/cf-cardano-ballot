package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.service.CardanoSubmitApiBlockchainTransactionSubmissionService;
import org.cardano.foundation.voting.service.TransactionSubmissionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    @Qualifier("l1-transaction-submission-service")
    public TransactionSubmissionService blockchainTransactionSubmissionService(HttpClient httpClient) {
        return new CardanoSubmitApiBlockchainTransactionSubmissionService(httpClient());
    }

}
