package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyVoteSignedWeb3Request extends SignedWeb3Request {

    @NotNull
    private String eventId;

}
