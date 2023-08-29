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
public class CheckVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String requestId;

    @NotBlank
    private String stakeAddress;

    @NotBlank
    private String verificationCode;

}
