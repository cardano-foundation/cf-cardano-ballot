package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.CategoryId;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.entity.ProposalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, ProposalId> {

//    @Query("SELECT p FROM Proposal p WHERE p.category.id = :categoryId AND p.name = :name")
//    Optional<Proposal> findProposalByName(
//            @Param("categoryId") CategoryId categoryId,
//            @Param("name") String name);

    @Query("DELETE FROM Proposal p WHERE p.absoluteSlot > :slot")
    @Modifying
    void deleteAllAfterSlot(@Param("slot") long slot);

}
