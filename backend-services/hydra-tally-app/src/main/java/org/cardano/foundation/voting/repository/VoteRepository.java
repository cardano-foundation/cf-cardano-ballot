package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.Vote;

import java.util.List;
import java.util.Set;

public interface VoteRepository {

    List<Vote> findAllVotes(byte[] eventId);

    List<Vote> findAllVotes(byte[] eventId, byte[] categoryId);

    Set<String> getAllUniqueCategories(byte[] eventId);

}
