package org.cardano.foundation.voting.domain.web3;

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
    private String proposalText;
    private String network;
    private long votedAt;
    private long votingPower;

}
