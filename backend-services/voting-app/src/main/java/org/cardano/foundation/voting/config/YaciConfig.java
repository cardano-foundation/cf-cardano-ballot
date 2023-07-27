package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.yaci.core.common.Constants;
import com.bloxbean.cardano.yaci.core.common.NetworkType;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YaciConfig {

    @Bean
    @Qualifier("yaci_well_known_point")
    public Point wellKnownPointForNetwork(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> Constants.WELL_KNOWN_MAINNET_POINT;
            case PREPROD -> Constants.WELL_KNOWN_PREPROD_POINT;
            case PREVIEW -> Constants.WELL_KNOWN_PREVIEW_POINT;
            case DEV -> throw new RuntimeException("Not implemented yet.");
        };
    }

    @Bean
    @Qualifier("yaci_network_type")
    public NetworkType networkType(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> NetworkType.MAINNET;
            case PREPROD -> NetworkType.PREPROD ;
            case PREVIEW -> NetworkType.PREVIEW;
            case DEV -> throw new RuntimeException("Not implemented yet.");
        };
    }

}
