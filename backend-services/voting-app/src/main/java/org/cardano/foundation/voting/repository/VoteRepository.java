package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {

    @Query("SELECT v FROM Vote v WHERE v.eventId = :eventId order by v.votedAtSlot, v.createdAt desc")
    List<Vote> findAllByEventId(@Param("eventId") String eventId);

    Optional<Vote> findByEventIdAndCategoryIdAndVoterStakingAddress(String eventId, String categoryId, String voterStakeAddress);

    @Query("SELECT COUNT(v) as totalVoteCount, SUM(v.votingPower) as totalVotingPower FROM Vote v WHERE v.eventId = :eventId")
    List<EventVoteCount> countAllByEventId(@Param("eventId") String eventId);

    @Query("SELECT p.id as proposalId, p.name as proposalName, COUNT(v) as totalVoteCount, SUM(v.votingPower) as totalVotingPower FROM Vote v, Proposal p WHERE v.proposalId = p.id AND v.eventId = :eventId AND v.categoryId = :categoryId GROUP BY p.id")
    List<EventCategoryVoteCount> countAllByEventId(@Param("eventId") String eventId, @Param("categoryId") String categoryId);

    interface EventVoteCount {
        long getTotalVoteCount();
        long getTotalVotingPower();
    }

    interface EventCategoryVoteCount {

        String getProposalId();

        String getProposalName();

        long getTotalVoteCount();

        long getTotalVotingPower();

    }

}
