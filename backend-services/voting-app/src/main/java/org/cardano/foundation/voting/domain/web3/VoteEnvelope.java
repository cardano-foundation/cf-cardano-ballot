package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;

@Builder
@Getter
public class VoteEnvelope {

    private String id;
    private String address;
    private String event;
    private String category;
    private String proposal;
    @Nullable
    private String proposalText;
    private String network;
    private String votedAt;

    @Nullable
    private String votingPower;

}
