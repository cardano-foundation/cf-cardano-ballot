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
public class DiscordStartVerificationRequest {

    @NotBlank
    private String secret;

    @NotBlank
    private String hashedDiscordId;

}
