package org.cardano.foundation.voting.domain.discord;

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
    private String discordIdHash;

    @NotBlank
    private String secret;

}
