package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class VerifyVoteRequest {

    @NotNull
    @Builder.Default
    private String uri = "/api/vote/verify";

    @NotNull
    @Builder.Default
    private String action = "VOTE_VERIFY";

    @NotNull
    private Instant timestamp;

    @NotNull
    private String eventId;

}
