package org.cardano.foundation.voting.domain.sms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cardano.foundation.voting.domain.WalletType;

import java.util.Locale;
import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
public class SMSStartVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String walletId;

    @NotNull
    private WalletType walletType;

    @NotBlank
    private String phoneNumber;

    @Builder.Default
    private Optional<Locale> locale = Optional.empty();

}
