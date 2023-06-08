package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignedVote {

    @NotNull
    private String voteId;

    @NotNull
    private String eventId;

    @NotNull
    private String categoryId;

    @NotNull
    private String proposalId;

    @NotNull
    private long votedAtSlot;

    @NotNull
    private Network network;

    @NotNull
    private String voterStakeAddress; // stakeAddress

    @NotNull
    private String coseSignature; // hex serialised

    @NotNull
    private String cosePublicKey; // hex serialised

}
