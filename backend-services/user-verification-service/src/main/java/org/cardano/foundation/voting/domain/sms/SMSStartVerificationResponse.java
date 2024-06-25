package org.cardano.foundation.voting.domain.sms;

import java.time.LocalDateTime;

public record SMSStartVerificationResponse(String eventId,
                                           String walletId,
                                           String requestId,
                                           LocalDateTime createdAt,
                                           LocalDateTime expiresAt) {
}
