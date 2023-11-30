package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.conversions.CardanoConverters;
import org.cardanofoundation.conversions.ClasspathConversionsFactory;
import org.cardanofoundation.conversions.domain.NetworkType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConvertersConfig {

    @Bean
    public CardanoConverters cardanoConverters(NetworkType networkType) {
        return ClasspathConversionsFactory.createConverters(networkType);
    }

    @Bean
    public NetworkType conversionsNetworkType(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> NetworkType.MAINNET;
            case PREPROD -> NetworkType.PREPROD;
            default -> throw new IllegalStateException("Unsupported network type: " + cardanoNetwork);
        };
    }

}
