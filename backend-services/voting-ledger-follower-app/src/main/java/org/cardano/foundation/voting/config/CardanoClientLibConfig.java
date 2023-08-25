package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import lombok.extern.slf4j.Slf4j;
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
    @Qualifier("yaci_blockfrost")
    public BackendService yaciBackendService() {
        return new BFBackendService("http://localhost:9090/yaci-api/", "");
    }

}
