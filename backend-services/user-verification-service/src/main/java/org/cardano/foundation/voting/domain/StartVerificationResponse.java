package org.cardano.foundation.voting.domain;

import java.time.LocalDateTime;

public record StartVerificationResponse(String eventId,
                                        String stakeAddress,
                                        String requestId,
                                        LocalDateTime createdAt,
                                        LocalDateTime expiresAt) {
}
