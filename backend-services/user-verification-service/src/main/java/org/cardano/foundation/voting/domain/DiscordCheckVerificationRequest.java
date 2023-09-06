package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
public class DiscordCheckVerificationRequest {

    @NotBlank
    private String secret;

    @NotBlank
    private String stakeAddress;

    @NotBlank
    private String signature;

    @Builder.Default
    private Optional<String> key = Optional.empty();

}
