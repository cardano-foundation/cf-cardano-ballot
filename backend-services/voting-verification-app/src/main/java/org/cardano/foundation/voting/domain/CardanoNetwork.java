package org.cardano.foundation.voting.domain;

import java.util.Arrays;
import java.util.List;

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
