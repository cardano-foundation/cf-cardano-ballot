package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {

    @Query("SELECT v.categoryId as categoryId, v.proposalId as proposalId FROM Vote v WHERE v.eventId = :eventId AND v.voterStakingAddress = :stakeAddress ORDER BY v.votedAtSlot, v.idNumericHash ASC")
    List<CategoryProposalProjection> getVotesByStakeAddress(@Param("eventId") String eventId, @Param("stakeAddress") String stakeAddress);

    @Query("SELECT v FROM Vote v WHERE v.eventId = :eventId ORDER BY v.votedAtSlot, v.idNumericHash ASC")
    List<CompactVote> findAllCompactVotesByEventId(@Param("eventId") String eventId);

    Optional<Vote> findByEventIdAndCategoryIdAndVoterStakingAddress(String eventId, String categoryId, String voterStakeAddress);

    @Query("SELECT COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId")
    List<HighLevelEventVoteCount> getHighLevelEventStats(@Param("eventId") String eventId);

    @Query("SELECT v.categoryId as categoryId, COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId GROUP BY categoryId")
    List<HighLevelCategoryLevelStats> getHighLevelCategoryLevelStats(@Param("eventId") String eventId);

    @Query("SELECT v.categoryId as categoryId, v.proposalId AS proposalId, COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId AND v.categoryId = :categoryId GROUP BY proposalId")
    List<CategoryLevelStats> getCategoryLevelStats(@Param("eventId") String eventId, @Param("categoryId") String categoryId);

    @Query("""
            SELECT v.event_id, v.category_id, v.proposal_id
            FROM Vote v
            WHERE v.event_id = :eventId
            AND v.category_id IN (
                SELECT DISTINCT v2.category_id
                FROM Vote v2
                WHERE v2.event_id = :eventId
            )
            GROUP BY v.event_id, v.category_id, v.proposal_id
            HAVING SUM(v.voting_power) = (
                SELECT MAX(category_sum)
                FROM (
                    SELECT v2.event_id, v2.category_id, SUM(v2.voting_power) AS category_sum
                    FROM Vote v2
                    WHERE v2.event_id = :eventId
                    GROUP BY v2.event_id, v2.category_id
                ) AS category_sums
                WHERE category_sums.event_id = v.event_id
                AND category_sums.category_id = v.category_id
            );""")
    List<EventWinnerStats> getWinners(@Param("eventId") String eventId);

    interface EventWinnerStats {

        String getEventId();

        String getCategoryId();

        String getProposalId();

    }

    interface CategoryProposalProjection {

        String getCategoryId();

        String getProposalId();

    }

    interface HighLevelEventVoteCount {

        @Nullable
        Long getTotalVoteCount();

        @Nullable
        Long getTotalVotingPower();

    }

    interface HighLevelCategoryLevelStats {

        String getCategoryId();

        @Nullable
        Long getTotalVoteCount();

        @Nullable
        Long getTotalVotingPower();

    }

    interface CategoryLevelStats {

        String getCategoryId();

        String getProposalId();

        @Nullable
        Long getTotalVoteCount();

        @Nullable
        Long getTotalVotingPower();

    }

    interface CompactVote {

        String getCoseSignature();

        Optional<String> getCosePublicKey();

    }

}
