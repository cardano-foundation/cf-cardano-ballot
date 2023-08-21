package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteMerkleProofRepository extends JpaRepository<VoteMerkleProof, String> {

    @Query("SELECT vmp FROM VoteMerkleProof vmp WHERE vmp.id = :id AND vmp.voteId = :voteId ORDER BY vmp.absoluteSlot, vmp.createdAt DESC")
    Optional<VoteMerkleProof> findLatestProof(@Param("id") String eventId, @Param("voteId") String voteId);

    @Query("UPDATE VoteMerkleProof vmp SET vmp.invalidated = true where vmp.absoluteSlot > :slot")
    @Modifying
    void invalidateMerkleProofsAfterSlot(@Param("slot") long slot);

}
