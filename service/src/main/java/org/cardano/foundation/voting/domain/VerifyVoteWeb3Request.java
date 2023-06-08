package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyVoteWeb3Request extends Web3Request {

    @NotNull
    private String eventId;

}
