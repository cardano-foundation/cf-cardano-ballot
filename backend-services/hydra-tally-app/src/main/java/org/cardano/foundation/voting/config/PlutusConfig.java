package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.plutus.api.PlutusObjectConverter;
import com.bloxbean.cardano.client.plutus.impl.DefaultPlutusObjectConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlutusConfig {

    @Bean
    public PlutusObjectConverter plutusObjectConverter() {
        return new DefaultPlutusObjectConverter();
    }

}
