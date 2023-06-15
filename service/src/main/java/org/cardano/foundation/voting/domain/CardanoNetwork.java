package org.cardano.foundation.voting.domain;

import com.google.common.base.Enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CardanoNetwork {

    MAIN, // main-net
    PREPROD, // preprod-net
    PREVIEW, // preview-net
    DEV; // e.g. locally hosted cardano-node

    public static List<String> supportedNetworks() {
        return Arrays.stream(CardanoNetwork.values()).map(network -> network.name().toLowerCase()).toList();
    }

    public static Optional<CardanoNetwork> fromName(String networkText) {
        return Enums.getIfPresent(CardanoNetwork.class, networkText.toUpperCase()).toJavaUtil();
    }

}
