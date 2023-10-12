package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.Vote;

import java.util.List;
import java.util.Set;

public interface VoteRepository {

    List<Vote> findAllVotes(String eventId);

    Set<String> getAllUniqueCategories(String eventId);

}
