package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteReceipt {

    private String id;

    private String event;

    private String category;

    private String proposal;

    private String voterStakingAddress;

    private String coseSignature;

    private String cosePublicKey;

    private long votingPower;

    private Network network;

    private long votedAtSlot;

}
