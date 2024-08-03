package org.cardano.foundation.voting.domain.discord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cardano.foundation.voting.domain.WalletType;

import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
public class DiscordCheckVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String walletId;

    @NotNull
    private WalletType walletType;

    @NotBlank
    private String secret;

    @Builder.Default
    protected Optional<String> signature = Optional.empty();

    @Builder.Default
    protected Optional<String> payload = Optional.empty();

    @Builder.Default
    protected Optional<String> publicKey = Optional.empty(); // StakeAddress (Cardano) or AID (KERI)

    @Builder.Default
    protected Optional<String> oobi = Optional.empty();

}
