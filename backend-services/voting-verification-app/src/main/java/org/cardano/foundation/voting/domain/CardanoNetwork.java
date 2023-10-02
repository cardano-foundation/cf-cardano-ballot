package org.cardano.foundation.voting.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cardano networks")
public enum CardanoNetwork {

    MAIN, // main-net
    PREPROD, // preprod-net
    PREVIEW, // preview-net
    DEV; // e.g. locally hosted cardano-node (Yaci-DevKit)

    public boolean isTestnet() {
        return !isMainnet();
    }

    public boolean isMainnet() {
        return this == MAIN;
    }

}
