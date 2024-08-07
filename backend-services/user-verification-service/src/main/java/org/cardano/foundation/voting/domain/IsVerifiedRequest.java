package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@Setter
@ToString
@AllArgsConstructor
public class IsVerifiedRequest {

    @NotBlank
    private String eventId;

    @NotNull
    private WalletType walletType;

    @NotBlank
    private String walletId;

}
