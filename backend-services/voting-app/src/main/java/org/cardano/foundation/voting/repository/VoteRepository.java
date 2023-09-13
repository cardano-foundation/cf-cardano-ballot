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

    @Query("SELECT v FROM Vote v WHERE v.eventId = :eventId ORDER BY v.votedAtSlot, v.createdAt DESC")
    List<Vote> findAllByEventId(@Param("eventId") String eventId);

    @Query("SELECT v FROM Vote v WHERE v.eventId = :eventId ORDER BY v.votedAtSlot, v.createdAt DESC")
    List<CompactVote> findAllCompactVotesByEventId(@Param("eventId") String eventId);

    Optional<Vote> findByEventIdAndCategoryIdAndVoterStakingAddress(String eventId, String categoryId, String voterStakeAddress);

    @Query("SELECT COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId")
    List<HighLevelEventVoteCount> getHighLevelEventStats(@Param("eventId") String eventId);

    @Query("SELECT v.categoryId as categoryId, COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId GROUP BY categoryId")
    List<HighLevelCategoryLevelStats> getHighLevelCategoryLevelStats(@Param("eventId") String eventId);

    @Query("SELECT v.categoryId as categoryId, v.proposalId AS proposalId, COUNT(v) AS totalVoteCount, SUM(v.votingPower) AS totalVotingPower FROM Vote v WHERE v.eventId = :eventId AND v.categoryId = :categoryId GROUP BY proposalId")
    List<CategoryLevelStats> getCategoryLevelStats(@Param("eventId") String eventId, @Param("categoryId") String categoryId);

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

        @Nullable String getCosePublicKey();

    }


}
