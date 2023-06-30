package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Networks;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardanoClientLibConfig {

    @Bean
    public BackendService backendService(@Value("${cardano-client-lib.backend.type}") String ccliBackendType,
                                         @Value("${blockfrost.url}") String blockfrostUrl,
                                         @Value("${blockfrost.api.key}") String blockfrostApiKey) {
        if ("BLOCKFROST".equalsIgnoreCase(ccliBackendType)) {
            return new BFBackendService(blockfrostUrl, blockfrostApiKey);
        }

        throw new RuntimeException("Add more CCL backends...");
    }

    @Bean
    @Qualifier("organiser_account")
    public Account organiserAccount(CardanoNetwork cardanoNetwork,
                                    @Value("${organiser.account.mnemonic}" ) String organiserMnemonic) {
        return switch(cardanoNetwork) {
            case MAIN -> new Account(Networks.mainnet(), organiserMnemonic);
            case PREPROD -> new Account(Networks.preprod(), organiserMnemonic);
            case PREVIEW -> new Account(Networks.preview(), organiserMnemonic);
            case DEV -> throw new RuntimeException("Organiser account not supported in DEV environment.");
        };
    }

}
