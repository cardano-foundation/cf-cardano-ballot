package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.client.AWSSNSClient;
import org.cardano.foundation.voting.service.sms.AWSSNSService;
import org.cardano.foundation.voting.service.sms.SMSService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SMSConfig {

    @Bean
    public SMSService smsService(AWSSNSClient awssnsClient) {
        return new AWSSNSService(awssnsClient);
    }

}
