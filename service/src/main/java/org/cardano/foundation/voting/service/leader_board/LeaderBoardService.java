package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.zalando.problem.Problem;

public interface LeaderBoardService {
    Either<Problem, Leaderboard> getLeaderboard(String networkName, String eventName);
}
