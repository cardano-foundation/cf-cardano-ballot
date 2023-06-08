package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
// CIP-93 compatibility
public class CastVoteRequest {

    @NotNull
    private String uri;

    @NotNull
    private String action;

    @NotNull
    private Instant timestamp;

    @NotNull
    private SignedVote vote;

}
