package org.cardano.foundation.voting.domain.web3;

import lombok.Getter;

@Getter
public class ViewVoteReceiptEnvelope {

    private String address;

    private String network;

    private String event;
    private String category;

}
