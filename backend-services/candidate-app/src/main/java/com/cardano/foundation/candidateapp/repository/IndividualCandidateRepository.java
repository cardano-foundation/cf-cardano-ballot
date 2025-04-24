package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.IndividualCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualCandidateRepository extends JpaRepository<IndividualCandidate, Long> {
}
