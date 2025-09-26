package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.WalletType;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {

    @Query("SELECT v FROM Vote v WHERE v.eventId = :eventId ORDER BY v.votedAtSlot, v.idNumericHash ASC")
    List<CompactVote> findAllCompactVotesByEventId(@Param("eventId") String eventId);

    interface CompactVote {

        String getId();

        String getSignature();

        WalletType getWalletType();

        Optional<String> getPayload();

        Optional<String> getPublicKey();

    }

}
