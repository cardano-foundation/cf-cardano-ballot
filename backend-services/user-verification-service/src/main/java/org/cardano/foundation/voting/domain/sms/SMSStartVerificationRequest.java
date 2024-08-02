package org.cardano.foundation.voting.domain.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;
import java.util.Optional;
import org.cardano.foundation.voting.utils.WalletType;

@Getter
@Builder
@Setter
@ToString
public class SMSStartVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String walletId;

    @Builder.Default
    private Optional<WalletType> walletType = Optional.of(WalletType.CARDANO);

    @NotBlank
    private String phoneNumber;

    @Builder.Default
    private Optional<Locale> locale = Optional.empty();
}
