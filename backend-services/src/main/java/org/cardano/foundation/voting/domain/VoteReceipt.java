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

    private CardanoNetwork cardanoNetwork;

    private Status status;

    private long votedAtSlot;

//    private VoteMerkleProof merkleProof;

    public enum Status {
        BASIC, // without merkle proof committed to L1 yet
        FULL // with merkle proof committed to L1
    }

}
