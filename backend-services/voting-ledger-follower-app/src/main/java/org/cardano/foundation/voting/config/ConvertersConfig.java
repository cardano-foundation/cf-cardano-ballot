package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.domain.ChainNetwork;
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
    public NetworkType conversionsNetworkType(ChainNetwork chainNetwork) {
        return switch(chainNetwork) {
            case MAIN -> NetworkType.MAINNET;
            case PREPROD -> NetworkType.PREPROD;
            default -> throw new IllegalStateException("Unsupported network type: " + chainNetwork);
        };
    }

}
