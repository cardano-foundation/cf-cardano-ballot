package org.cardano.foundation.voting.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.domain.WalletType;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class VerificationResult {

    private final String message;
    private final WalletType walletType;
    private final Optional<String> walletId;

}
