package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.Vote;

import java.util.List;

public interface VoteRepository {

    List<Vote> findAllVotes(String eventId);

    // TODO pagination support

}
