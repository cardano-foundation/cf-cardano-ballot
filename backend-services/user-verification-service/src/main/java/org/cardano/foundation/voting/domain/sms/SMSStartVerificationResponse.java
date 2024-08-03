package org.cardano.foundation.voting.domain.sms;

import org.cardano.foundation.voting.domain.WalletType;
import java.time.LocalDateTime;

public record SMSStartVerificationResponse(String eventId,
                                           String walletId,
                                           WalletType walletType,
                                           String requestId,
                                           LocalDateTime createdAt,
                                           LocalDateTime expiresAt) {
}
