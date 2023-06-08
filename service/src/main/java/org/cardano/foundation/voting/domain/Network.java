package org.cardano.foundation.voting.domain;

public enum Network {

    MAINNET("main"),
    PREPROD("preprod"),
    PREVIEW("preview"),
    DEVNET("dev");

    private String network;

    private Network(String network) {
        this.network = network;
    }

}
