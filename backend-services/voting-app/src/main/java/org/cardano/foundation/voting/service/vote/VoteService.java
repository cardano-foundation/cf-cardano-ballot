package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.UserVotes;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.service.auth.jwt.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.zalando.problem.Problem;

import java.util.List;

public interface VoteService {

    Either<Problem, List<UserVotes>> getVotes(JwtAuthenticationToken auth);

    Either<Problem, Boolean> isVoteChangingPossible(String voteId, JwtAuthenticationToken auth);

    Either<Problem, Vote> castVote(Web3AuthenticationToken web3AuthenticationToken);

    Either<Problem, VoteReceipt> voteReceipt(Web3AuthenticationToken web3AuthenticationToken);

    Either<Problem, VoteReceipt> voteReceipt(String categoryId, JwtAuthenticationToken auth);

}
