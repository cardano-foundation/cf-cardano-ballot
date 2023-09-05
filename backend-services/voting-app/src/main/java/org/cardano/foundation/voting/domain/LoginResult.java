package org.cardano.foundation.voting.domain;

import java.time.LocalDateTime;

public record LoginResult(String accessToken, LocalDateTime expiresAt) {
}
