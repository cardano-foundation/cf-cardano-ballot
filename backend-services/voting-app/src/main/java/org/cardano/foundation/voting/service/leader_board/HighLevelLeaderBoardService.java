package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.zalando.problem.Problem;

public interface HighLevelLeaderBoardService {

    Either<Problem, Boolean> isHighLevelEventLeaderboardAvailable(String event, boolean forceLeaderboard);

    Either<Problem, Boolean> isHighLevelCategoryLeaderboardAvailable(String event, boolean forceLeaderboard);

    Either<Problem, Leaderboard.ByEventStats> getEventLeaderboard(String event, boolean forceLeaderboard);

}
