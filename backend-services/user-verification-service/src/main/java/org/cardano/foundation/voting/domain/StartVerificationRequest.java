package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@Setter
@ToString
public class StartVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String stakeAddress;

    @NotBlank
    private String phoneNumber;

}
