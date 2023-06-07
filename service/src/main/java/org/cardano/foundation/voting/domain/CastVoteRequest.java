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
    @Builder.Default
    private String uri = "/api/vote/cast";

    @NotNull
    @Builder.Default
    private String action = "CAST_VOTE";

    @NotNull
    private Instant timestamp;

    @NotNull
    private SignedVote signedVote;

}
