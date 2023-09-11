package org.cardano.foundation.voting.domain;

import java.time.LocalDateTime;

public record SMSStartVerificationResponse(String eventId,
                                           String stakeAddress,
                                           String requestId,
                                           LocalDateTime createdAt,
                                           LocalDateTime expiresAt) {
}
