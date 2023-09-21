package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.Leaderboard;

import java.util.List;

public interface CustomVoteRepository {

    List<Leaderboard.WinnerStats> getEventWinners(String eventId, List<String> categoryIds);

}
