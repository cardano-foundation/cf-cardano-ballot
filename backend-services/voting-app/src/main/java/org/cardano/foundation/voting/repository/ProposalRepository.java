package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, String> {

    @Query("SELECT p FROM Proposal p WHERE p.category.id = ?1 AND p.name = ?2")
    Optional<Proposal> findProposalByName(String category, String name);

}
