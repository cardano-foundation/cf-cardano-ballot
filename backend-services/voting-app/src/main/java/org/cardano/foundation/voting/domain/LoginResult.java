package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.domain.web3.WalletType;

import java.time.LocalDateTime;

public record LoginResult(String accessToken,
                          WalletType walletType,
                          LocalDateTime expiresAt) {
}
