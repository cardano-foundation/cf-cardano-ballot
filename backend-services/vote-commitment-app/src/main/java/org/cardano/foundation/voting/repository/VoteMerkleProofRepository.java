package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteMerkleProofRepository extends JpaRepository<VoteMerkleProof, String> {

    @Modifying
    @Query("UPDATE VoteMerkleProof vmp SET vmp.invalidated = true WHERE vmp.eventId = :eventId AND vmp.absoluteSlot > :slot")
    int invalidateMerkleProofsAfterSlot(@Param("eventId") String eventId, @Param("slot") long slot);

    List<VoteMerkleProof> findTop1ByEventIdAndInvalidatedOrderByCreatedAtDesc(String eventId, boolean invalidated);

}
