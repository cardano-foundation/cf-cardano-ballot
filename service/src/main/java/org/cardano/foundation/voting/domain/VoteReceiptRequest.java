package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class VoteReceiptRequest {

    @NotNull
    @Builder.Default
    private String uri = "/api/vote/receipt";

    @NotNull
    @Builder.Default
    private String action = "VOTE_RECEIPT";

    @NotNull
    private Instant timestamp;

    @NotNull
    private String eventId;

}
