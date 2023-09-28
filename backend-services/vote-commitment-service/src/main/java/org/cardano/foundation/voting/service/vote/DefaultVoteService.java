package org.cardano.foundation.voting.service.vote;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultVoteService implements VoteService {

    private final VoteRepository voteRepository;

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "service.vote.findAllCompactVotesByEventId", histogram = true)
    public List<VoteRepository.CompactVote> findAllCompactVotesByEventId(String eventId) {
        return voteRepository.findAllCompactVotesByEventId(eventId);
    }

}
