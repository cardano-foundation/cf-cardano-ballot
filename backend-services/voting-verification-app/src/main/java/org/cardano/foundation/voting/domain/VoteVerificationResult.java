package org.cardano.foundation.voting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Result object for vote verification")
public class VoteVerificationResult {

    @Schema(description = "Indicates if the vote is verified or not", example = "true")
    private boolean isVerified;

    @Schema(description = "Cardano network ", required = true)
    private CardanoNetwork network;

}
