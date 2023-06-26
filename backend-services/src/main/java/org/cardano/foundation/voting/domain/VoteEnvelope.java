package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VoteEnvelope {

    private String id;
    private String address;
    private String event;
    private String category;
    private String proposal;
    private String network;
    private String votedAt;
    private long votingPower;

}
