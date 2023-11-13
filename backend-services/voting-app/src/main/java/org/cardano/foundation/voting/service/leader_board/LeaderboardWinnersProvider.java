package org.cardano.foundation.voting.service.leader_board;

import org.cardano.foundation.voting.domain.WinnerLeaderboardSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardWinnersProvider {

    @Autowired
    @Qualifier("db_leaderboard_winners_service")
    private LeaderboardWinnersService dbLeaderboardWinnersService;

    @Autowired
    @Qualifier("l1_leaderboard_winners_service")
    private LeaderboardWinnersService l1LeaderboardWinnersService;

    public LeaderboardWinnersService getWinnerLeaderboardSource(WinnerLeaderboardSource winnerLeaderboardSource) {
        return switch (winnerLeaderboardSource) {
            case db:
                yield dbLeaderboardWinnersService;
            case l1:
                yield l1LeaderboardWinnersService;
        };
    }


}
