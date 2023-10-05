package org.cardano.foundation.voting.service.vote;

import org.cardano.foundation.voting.repository.VoteRepository;

import java.util.List;

public interface VoteService {

    List<VoteRepository.CompactVote> findAllCompactVotesByEventId(String eventId);

}
