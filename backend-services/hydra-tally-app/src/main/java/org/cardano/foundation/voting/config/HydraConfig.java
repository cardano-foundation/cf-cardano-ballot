package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.HydraTxSubmissionService;
import org.cardano.foundation.voting.service.TransactionSubmissionService;
import org.cardanofoundation.hydra.cardano.client.lib.HydraNodeProtocolParametersAdapter;
import org.cardanofoundation.hydra.cardano.client.lib.SnapshotUTxOSupplier;
import org.cardanofoundation.hydra.core.store.InMemoryUTxOStore;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.cardanofoundation.hydra.reactor.HydraReactiveWebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@Slf4j
public class HydraConfig {

    @Bean
    public UTxOStore uTxOStore() {
        return new InMemoryUTxOStore();
    }

    @Bean
    public UtxoSupplier snapshotUTxOSupplier(UTxOStore uTxOStore) {
        return new SnapshotUTxOSupplier(uTxOStore);
    }

    @Bean
    public HydraReactiveClient hydraReactiveClient(UTxOStore uTxOStore,
                                                   @Value("${hydra.ws.url}") String hydraWsUrl) {
        return new HydraReactiveClient(uTxOStore, hydraWsUrl);
    }

    @Bean
    public HydraReactiveWebClient hydraWebClient(HttpClient httpClient,
                                                @Value("${hydra.http.url}") String hydraHttpUrl) {
        return new HydraReactiveWebClient(httpClient, hydraHttpUrl);
    }

    @Bean
    public ProtocolParamsSupplier protocolParamsSupplier(HydraReactiveWebClient hydraReactiveWebClient) {
        var hydraProtocolParameters = hydraReactiveWebClient.fetchProtocolParameters()
                .block(Duration.ofMinutes(1));

        return new HydraNodeProtocolParametersAdapter(hydraProtocolParameters);
    }

    @Bean
    @Qualifier("hydra-transaction-submission-service")
    public TransactionSubmissionService hydraTransactionSubmissionService(HydraReactiveClient hydraReactiveClient) {
        return new HydraTxSubmissionService(hydraReactiveClient);
    }

}
