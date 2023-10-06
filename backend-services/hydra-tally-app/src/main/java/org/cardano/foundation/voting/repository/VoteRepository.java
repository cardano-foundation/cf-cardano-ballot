package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.CompactVote;

import java.util.List;

public interface VoteRepository {

    List<CompactVote> findAllVotes(String eventId);

    // TODO pagination support

}
