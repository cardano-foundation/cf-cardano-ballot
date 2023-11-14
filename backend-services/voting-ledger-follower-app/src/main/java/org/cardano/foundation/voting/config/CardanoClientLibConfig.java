package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CardanoClientLibConfig {

    @Bean
    @Qualifier("original_blockfrost")
    public BackendService orgBackendService(@Value("${blockfrost.url}") String blockfrostUrl,
                                            @Value("${blockfrost.api.key}") String blockfrostApiKey) {
        return new BFBackendService(blockfrostUrl, blockfrostApiKey);
    }

    @Bean
    public Network network(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> Networks.mainnet();
            case PREPROD -> Networks.preprod();
            case PREVIEW -> Networks.preview();
            case DEV -> Networks.testnet();
        };
    }

    @Bean
    @Qualifier("yaci_blockfrost")
    public BackendService yaciBackendService() {
        return new BFBackendService("http://localhost:9090/yaci-api/", "");
    }

}
