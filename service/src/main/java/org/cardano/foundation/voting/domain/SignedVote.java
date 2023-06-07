package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.cardano.foundation.voting.domain.Network;

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
    private String votedAtSlot;

    @NotNull
    private Network network;

    @NotNull
    private String voterStakeAddress; // stakeAddress

    @NotNull
    private String coseSignature;

    @NotNull
    private String cosePublicKey;

}
