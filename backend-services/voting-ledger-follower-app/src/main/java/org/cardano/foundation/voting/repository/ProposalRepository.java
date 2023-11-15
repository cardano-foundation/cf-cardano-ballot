package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, String> {

    @Query("SELECT p FROM Proposal p WHERE p.category.id = :category AND p.name = :name")
    Optional<Proposal> findProposalByName(@Param("category") String category, @Param("name") String name);

    @Query("DELETE FROM Proposal p WHERE p.absoluteSlot > :slot")
    @Modifying
    int deleteAllAfterSlot(@Param("slot") long slot);

}
