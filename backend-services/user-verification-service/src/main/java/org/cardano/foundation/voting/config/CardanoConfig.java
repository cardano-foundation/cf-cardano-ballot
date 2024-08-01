package org.cardano.foundation.voting.config;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CardanoConfig {

    @Bean
    public ChainNetwork network(@Value("${cardano.network:main}") ChainNetwork network) {
        log.info("Configured backend network:{}", network);

        return network;
    }

}
