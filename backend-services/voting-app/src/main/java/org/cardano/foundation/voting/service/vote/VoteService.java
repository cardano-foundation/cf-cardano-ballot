package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.auth.JwtAuthenticationToken;
import org.zalando.problem.Problem;

import java.util.List;

public interface VoteService {

    /**
     * Return true if the slot is within permissible range
     */
    List<Vote> findAll(String eventId);

    Either<Problem, Boolean> isVoteCastingStillPossible(String eventId, String voteId);

    Either<Problem, Vote> castVote(SignedWeb3Request castVoteRequest);

    Either<Problem, VoteReceipt> voteReceipt(SignedWeb3Request viewVoteReceiptRequest);

    Either<Problem, VoteReceipt> voteReceipt(JwtAuthenticationToken jwtAuthenticationToken, String eventId, String categoryId);

}
