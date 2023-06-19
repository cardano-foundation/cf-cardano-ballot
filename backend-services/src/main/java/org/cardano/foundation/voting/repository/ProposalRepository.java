package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, String> {

    Optional<Proposal> findByName(String name);

}
