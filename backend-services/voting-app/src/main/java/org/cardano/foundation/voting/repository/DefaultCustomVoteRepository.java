package org.cardano.foundation.voting.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultCustomVoteRepository implements CustomVoteRepository {

    @PersistenceContext
    private EntityManager entityManager;


    // TODO optimise this in one big and more complex query for all categories?
    public List<Leaderboard.WinnerStats> getEventWinners(String eventId, List<String> categoryIds) {
        var winningProposals = new ArrayList<Leaderboard.WinnerStats>();

        for (var categoryId : categoryIds) {
            var winningQuery = entityManager.createQuery(
                    "SELECT v.proposalId, SUM(v.votingPower) AS totalPower, COUNT(v.id) AS votesCount " +
                            "FROM Vote v " +
                            "WHERE v.eventId = :eventId AND v.categoryId = :categoryId " +
                            "GROUP BY v.proposalId " +
                            "ORDER BY totalPower, votesCount DESC",
                    Object[].class);

            winningQuery.setParameter("eventId", eventId);
            winningQuery.setParameter("categoryId", categoryId);
            winningQuery.setMaxResults(1);

            var result = winningQuery.getResultList();

            if (result.isEmpty()) {
                return winningProposals;
            }

            var winner = result.get(0);
            var proposalId = (String) winner[0];

            var winningVote = Leaderboard.WinnerStats.builder()
                    .categoryId(categoryId)
                    .proposalId(proposalId)
                    .build();

            winningProposals.add(winningVote);
        }

        return winningProposals;
    }

}
