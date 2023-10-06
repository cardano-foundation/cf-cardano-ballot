package org.cardano.foundation.voting.domain;

import java.util.Optional;

public record CompactVote(
        String voteId,
        String eventId,
        String coseSignature,
        Optional<String> cosePublicKey) { }
