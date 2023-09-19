package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@Setter
@ToString
@AllArgsConstructor
public class IsVerifiedRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String stakeAddress;

}
