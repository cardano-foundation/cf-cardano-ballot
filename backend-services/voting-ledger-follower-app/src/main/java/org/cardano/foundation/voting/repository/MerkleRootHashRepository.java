package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.MerkleRootHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerkleRootHashRepository extends JpaRepository<MerkleRootHash, String> {

    @Query("SELECT mrh FROM MerkleRootHash mrh WHERE mrh.eventId = :eventId AND mrh.id = :id")
    Optional<MerkleRootHash> findByEventIdAndId(@Param("eventId") String eventId, @Param("id") String id);

    @Query("DELETE FROM MerkleRootHash mrh WHERE mrh.absoluteSlot > :slot")
    @Modifying
    void deleteAllAfterSlot(@Param("slot") long slot);

}
