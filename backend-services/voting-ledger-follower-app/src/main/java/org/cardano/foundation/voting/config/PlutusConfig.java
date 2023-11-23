package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlutusConfig {

    @Bean
    public CategoryResultsDatumConverter categoryResultsDatumConverter() {
        return new CategoryResultsDatumConverter();
    }

}
