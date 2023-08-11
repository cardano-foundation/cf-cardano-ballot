package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import org.cardano.foundation.voting.service.blockchain_state.*;
import org.cardano.foundation.voting.service.blockchain_state.blockfrost.BlockfrostBlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.cardano_submit_api.CardanoSubmitApiBlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Bean
    public BlockchainDataChainTipService blockchainDataChainTipService(BackendService backendService) {
        return new BackendServiceBlockchainDataChainTipService(backendService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.ingestion.strategy", havingValue = "PUSH")
    public BlockchainDataMetadataService pushBasedBlockchainDataMetadataService() {
        return new BlockchainDataMetadataService() { }; // when app.ingestion.strategy is PUSH, we don't really need to have this service
    }

    @Bean
    @ConditionalOnProperty(name = "app.ingestion.strategy", havingValue = "PULL") // when we are pull based we need BlockchainDataMetadataService
    public BlockchainDataMetadataService pullBasedBlockchainDataMetadataService(
            BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService,
            @Value("${blockfrost.api.key}") String blockfrostProjectId,
            @Value("${blockfrost.url}") String blockfrostUrl
            ) {

        return new BlockfrostBlockchainDataMetadataService(
                blockchainDataTransactionDetailsService,
                blockfrostProjectId,
                blockfrostUrl);
    }

    @Bean
    @Profile( value = { "prod", "dev--preprod"} )
    public BlockchainDataStakePoolService blockchainDataStakePoolService(BackendService backendService) {
        return new BackendServiceBlockchainDataStakePoolService(backendService);
    }

    @Bean
    @Profile( value = "dev--yaci-dev-kit" )
    public BlockchainDataStakePoolService dummyBlockchainDataStakePoolService() {
        return new FixedBlockchainDataStakePoolService(1000);
    }

    @Bean
    public BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService(BackendService backendService) {
        return new BackendServiceBlockchainDataTransactionDetailsService(backendService);
    }

}
