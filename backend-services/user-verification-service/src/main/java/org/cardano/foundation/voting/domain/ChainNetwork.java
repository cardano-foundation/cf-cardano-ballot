package org.cardano.foundation.voting.domain;

import java.util.Arrays;
import java.util.List;

public enum ChainNetwork {

    MAIN, // main-net
    PREPROD, // preprod-net
    PREVIEW, // preview-net
    DEV; // e.g. local Yaci-Dev net

    public static List<String> supportedNetworks() {
        return Arrays.stream(ChainNetwork.values()).map(network -> network.name().toLowerCase()).toList();
    }

    public boolean isTestnet() {
        return !isMainnet();
    }

    public boolean isMainnet() {
        return this == MAIN;
    }

}
