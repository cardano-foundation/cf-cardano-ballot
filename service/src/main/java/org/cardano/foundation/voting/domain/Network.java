package org.cardano.foundation.voting.domain;

public enum Network {

    MAINNET("mainnet"),
    PREPROD("preprod");

    private String network;

    private Network(String network) {
        this.network = network;
    }

}
