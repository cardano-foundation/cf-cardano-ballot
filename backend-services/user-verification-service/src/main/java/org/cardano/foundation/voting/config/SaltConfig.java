package org.cardano.foundation.voting.config;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.sms.SaltHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SaltConfig {

    @Value("${phone.number.salt}")
    private String phoneNumberSalt;

    @Bean
    public SaltHolder salt() {
        return new SaltHolder(this.phoneNumberSalt);
    }

}
