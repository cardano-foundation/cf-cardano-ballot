package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.yaci.store.common.domain.NetworkType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CardanoNetwork {

    MAIN(Optional.of(NetworkType.MAINNET)), // main-net
    PREPROD(Optional.of(NetworkType.PREPROD)), // preprod-net
    PREVIEW(Optional.of(NetworkType.PREVIEW)), // preview-net
    DEV(Optional.empty()); // e.g. locally hosted cardano-node

    private final Optional<NetworkType> networkType;

    CardanoNetwork(Optional<NetworkType> networkType) {
        this.networkType = networkType;
    }

    public Optional<NetworkType> getNetworkType() {
        return networkType;
    }

    public static List<String> supportedNetworks() {
        return Arrays.stream(CardanoNetwork.values()).map(network -> network.name().toLowerCase()).toList();
    }

    public boolean isTestnet() {
        return !isMainnet();
    }

    public boolean isMainnet() {
        return this == MAIN;
    }

}
