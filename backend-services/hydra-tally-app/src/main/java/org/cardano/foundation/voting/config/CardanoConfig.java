package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.hydra.cardano.client.lib.CardanoOperator;
import org.cardanofoundation.hydra.cardano.client.lib.CardanoOperatorSupplier;
import org.cardanofoundation.hydra.cardano.client.lib.JacksonClasspathSecretKeyCardanoOperatorSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CardanoConfig {

    @Bean
    public CardanoNetwork network(@Value("${cardano.network:main}") CardanoNetwork network) {
        log.info("Configured backend network:{}", network);

        return network;
    }

    @Bean
    public Network cardanoNetwork(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> Networks.mainnet();
            case PREPROD -> Networks.preprod();
            case PREVIEW -> Networks.preview();
            case DEV -> Networks.testnet();
        };
    }

    @Bean
    public CardanoOperator l1CardanoOperator(//@Qualifier("l1-operator-supplier")
                                             CardanoOperatorSupplier cardanoOperatorSupplier) {
        var op =  cardanoOperatorSupplier.getOperator();

        log.info("L1 operator address: {}", op.getAddress());

        return op;
    }

    @Bean
    public CardanoOperatorSupplier l1SecretKeySupplier(@Value("${l1.operator.secret.file.path}") String secretFilePath,
                                                     Network network) throws CborSerializationException {
        log.info("L1 Secret file path: {}", secretFilePath);

        return new JacksonClasspathSecretKeyCardanoOperatorSupplier(secretFilePath, network);
    }

}
