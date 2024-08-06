package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Networks;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CardanoClientLibConfig {

    @Bean
    public BackendService backendService(@Value("${blockfrost.url}") String blockfrostUrl,
                                         @Value("${blockfrost.api.key}") String blockfrostApiKey) {
        return new BFBackendService(blockfrostUrl, blockfrostApiKey);
    }

    @Bean
    @Qualifier("organiser_account")
    public Account organiserAccount(ChainNetwork chainNetwork,
                                    @Value("${organiser.account.mnemonic}" ) String organiserMnemonic) {
        var organiserAccount = switch(chainNetwork) {
            case MAIN -> new Account(Networks.mainnet(), organiserMnemonic);
            case PREPROD -> new Account(Networks.preprod(), organiserMnemonic);
            case PREVIEW -> new Account(Networks.preview(), organiserMnemonic);
            case DEV -> new Account(Networks.testnet(), organiserMnemonic);
        };

        log.info("Organiser's address:{}, stakeAddress:{}", organiserAccount.baseAddress(), organiserAccount.stakeAddress());

        return organiserAccount;
    }

}
