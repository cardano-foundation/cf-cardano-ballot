package org.cardano.foundation.voting.domain;

public record ExpirationData(String id,
                             boolean notStarted,
                             boolean finished,
                             boolean active,
                             boolean proposalsReveal) {
}
