package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Qualifier("l1_leaderboard_winners_service")
public class L1LeaderboardWinnersService extends AbstractWinnersService implements LeaderboardWinnersService {

    @Override
    public Either<Problem, Leaderboard.ByProposalsInCategoryStats> getCategoryLeaderboard(String event, String category, boolean forceLeaderboard) {
        // for our category we have to know which contract address is responsible
        // we could read contract address from category on chain event registration data
        //

        return Either.left(Problem.builder()
                .withTitle("UNRECOGNISED_CATEGORY")
                .withDetail("Unrecognised category, category:" + category)
                .withStatus(BAD_REQUEST)
                .build()
        );
    }

}
