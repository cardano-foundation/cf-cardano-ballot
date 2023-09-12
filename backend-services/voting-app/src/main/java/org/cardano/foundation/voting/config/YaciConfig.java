package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.yaci.core.common.Constants;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ProtocolMagic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.bloxbean.cardano.yaci.core.common.NetworkType.*;

@Configuration
@ConditionalOnProperty(name = "rollback.handling.enabled", havingValue = "true")
public class YaciConfig {

    @Bean
    public Point wellKnownPointForNetwork(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> Constants.WELL_KNOWN_MAINNET_POINT;
            case PREPROD -> Constants.WELL_KNOWN_PREPROD_POINT;
            case PREVIEW -> Constants.WELL_KNOWN_PREVIEW_POINT;
            case DEV -> null;
        };
    }

    @Bean
    public ProtocolMagic protocolMagic(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> new ProtocolMagic(MAINNET.getProtocolMagic());
            case PREPROD -> new ProtocolMagic(PREPROD.getProtocolMagic());
            case PREVIEW -> new ProtocolMagic(PREVIEW.getProtocolMagic());
            case DEV -> new ProtocolMagic(42);
        };
    }

}
