package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.zalando.problem.Problem;

import java.util.Optional;

public interface LeaderboardWinnersService {

    Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event,
                                                            String category,
                                                            boolean forceLeaderboard);

    Either<Problem, Optional<Leaderboard.ByProposalsInCategoryStats>> getCategoryLeaderboard(String event,
                                                                                             String category,
                                                                                             boolean forceLeaderboard);

}
