package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.WellKnownPointWithProtocolMagic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.bloxbean.cardano.yaci.core.common.Constants.*;
import static com.bloxbean.cardano.yaci.core.common.NetworkType.*;

@Configuration
public class YaciConfig {

    @Bean
    @Nullable
    public WellKnownPointWithProtocolMagic wellKnownPointForNetwork(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> new WellKnownPointWithProtocolMagic(Optional.of(WELL_KNOWN_MAINNET_POINT), MAINNET.getProtocolMagic());
            case PREPROD -> new WellKnownPointWithProtocolMagic(Optional.of(WELL_KNOWN_PREPROD_POINT), PREPROD.getProtocolMagic());
            case PREVIEW -> new WellKnownPointWithProtocolMagic(Optional.of(WELL_KNOWN_PREVIEW_POINT), PREVIEW.getProtocolMagic());
            case DEV -> new WellKnownPointWithProtocolMagic(Optional.empty(), 42);
        };
    }

}
