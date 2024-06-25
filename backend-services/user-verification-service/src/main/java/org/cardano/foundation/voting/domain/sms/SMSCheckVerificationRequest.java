package org.cardano.foundation.voting.domain.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;
import java.util.Optional;

@Getter
@Builder
@Setter
@ToString
public class SMSCheckVerificationRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String requestId;

    @NotBlank
    private String walletId;

    @Builder.Default
    private Optional<String> walletIdType = Optional.empty();

    @NotBlank
    private String verificationCode;

    @Builder.Default
    private Optional<Locale> locale = Optional.empty();

}
