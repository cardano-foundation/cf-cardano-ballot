package org.cardano.foundation.voting.repository;

import org.cardano.foundation.voting.domain.entity.ProposalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalDetailsRepository extends JpaRepository<ProposalDetails, String> {
}
