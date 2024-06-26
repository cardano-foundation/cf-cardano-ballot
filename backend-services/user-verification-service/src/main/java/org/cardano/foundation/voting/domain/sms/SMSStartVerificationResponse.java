package org.cardano.foundation.voting.domain.sms;

import org.cardano.foundation.voting.utils.WalletType;
import java.time.LocalDateTime;

public record SMSStartVerificationResponse(String eventId,
                                           String walletId,
                                           WalletType walletIdType,
                                           String requestId,
                                           LocalDateTime createdAt,
                                           LocalDateTime expiresAt) {
}
