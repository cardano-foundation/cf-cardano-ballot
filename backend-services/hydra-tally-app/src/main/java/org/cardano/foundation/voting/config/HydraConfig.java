package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.HydraTransactionClient;
import org.cardanofoundation.hydra.cardano.client.lib.*;
import org.cardanofoundation.hydra.client.HydraClientOptions;
import org.cardanofoundation.hydra.core.store.InMemoryUTxOStore;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HydraConfig {

    @Bean
    public HydraClientOptions hydraClientOptions(UTxOStore uTxOStore,
                                                 @Value("${hydra.ws.url}") String hydraWsUrl) {
        return HydraClientOptions.builder(hydraWsUrl)
                .withUTxOStore(uTxOStore)
                .history(false)
                .build();
    }

    @Bean
    public HydraTransactionClient hydraTransactionClient(UTxOStore uTxOStore,
                                                         HydraClientOptions hydraClientOptions) {
        return new HydraTransactionClient(uTxOStore, hydraClientOptions);
    }

    @Bean
    public ProtocolParamsSupplier protocolParamsSupplier(ObjectMapper objectMapper,
                                                         @Value("${hydra.protocol.parameters.path}") String hydraProtocolParametersPath) {
        return new JacksonClasspathProtocolParametersSupplier(objectMapper, hydraProtocolParametersPath);
    }

    @Bean
    public UTxOStore uTxOStore() {
        return new InMemoryUTxOStore();
    }

    @Bean
    public UtxoSupplier snapshotUTxOSupplier(UTxOStore uTxOStore) {
        return new SnapshotUTxOSupplier(uTxOStore);
    }

    @Bean
    public HydraOperator hydraOperator(HydraOperatorSupplier hydraOperatorSupplier) {
        var op =  hydraOperatorSupplier.getOperator();

        log.info("Hydra's operator address: {}", op.getAddress());

        return op;
    }

    @Bean
    public HydraOperatorSupplier hydraOperatorSupplier(ObjectMapper objectMapper,
                                                       @Value("${hydra.operator.secret.file.path}") String hydraSecretFilePath,
                                                       Network network) throws CborSerializationException {

        log.info("Hydra's secret file path: {}", hydraSecretFilePath);

        return new JacksonClasspathSecretKeySupplierHydra(objectMapper, hydraSecretFilePath, network);
    }

}
