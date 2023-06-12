package org.cardano.foundation.voting.domain;

import com.google.common.base.Enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Network {

    MAIN,
    PREPROD,
    PREVIEW,
    DEV;

    public static List<String> supportedNetworks() {
        return Arrays.stream(Network.values()).map(network -> network.name().toLowerCase()).toList();
    }

    public static Optional<Network> fromName(String networkText) {
        return Enums.getIfPresent(Network.class, networkText.toUpperCase()).toJavaUtil();
    }

}
