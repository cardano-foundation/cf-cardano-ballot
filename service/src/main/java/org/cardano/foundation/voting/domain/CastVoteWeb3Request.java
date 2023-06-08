package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CastVoteWeb3Request extends Web3Request {

    @NotNull
    private SignedVote vote;

}
