package org.cardano.foundation.voting.service.vote;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.RootHash;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

public interface VoteService {
    /**
     * Return true if the slot is within permissible range
     */
    List<Vote> findAll(Event event);

    @Transactional
    @Timed(value = "service.vote.isVoteCastingStillPossible", percentiles = {0.3, 0.5, 0.95})
    boolean isVoteCastingStillPossible(String event, String category, String stakeAddress);

    @Transactional
    @Timed(value = "service.vote.castVote", percentiles = {0.3, 0.5, 0.95})
    Either<Problem, Vote> castVote(SignedWeb3Request castVoteRequest);

    @Transactional
    @Timed(value = "service.vote.voteReceipt", percentiles = {0.3, 0.5, 0.95})
    Either<Problem, VoteReceipt> voteReceipt(String event, String category, String stakeAddress);

    @Transactional
    RootHash storeLatestRootHash(Event event);

    @Transactional
    Optional<RootHash> getRootHash(String eventId);
}
