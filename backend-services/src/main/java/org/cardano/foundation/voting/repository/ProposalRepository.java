package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, String> {

}
