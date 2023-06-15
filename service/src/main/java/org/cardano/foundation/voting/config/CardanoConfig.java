package org.cardano.foundation.voting.config;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CardanoConfig {

    @Bean
    public CardanoNetwork network(@Value("${cardano.network:main}") String networkName) {
        var network = CardanoNetwork.fromName(networkName)
                .orElseThrow(() -> new RuntimeException("Invalid network name: " + networkName));

        log.info("Configured backend network:{}", network);

        return network;
    }

}
