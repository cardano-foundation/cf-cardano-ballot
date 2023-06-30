package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteMerkleProofRepository extends JpaRepository<VoteMerkleProof, String> {

    @Query("SELECT vmp FROM VoteMerkleProof vmp WHERE vmp.eventId = ?1 AND vmp.voteId = ?2 ORDER BY vmp.createdAt DESC")
    Optional<VoteMerkleProof> findLatestProof(String eventId, String voteId);

}
