package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

public interface VoteService {

    /**
     * Return true if the slot is within permissible range
     */
    List<Vote> findAll(String eventId);

    // TODO is this ok to assume uuid4 to be globally unique across all events
    Optional<Vote> findById(String voteId);

    Either<Problem, Boolean> isVoteCastingStillPossible(String event, String voteId);

    Either<Problem, Vote> castVote(SignedWeb3Request castVoteRequest);

    Either<Problem, VoteReceipt> voteReceipt(SignedWeb3Request viewVoteReceiptRequest);

}
