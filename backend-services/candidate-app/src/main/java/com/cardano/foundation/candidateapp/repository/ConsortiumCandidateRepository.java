package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.ConsortiumCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsortiumCandidateRepository extends JpaRepository<ConsortiumCandidate, Long> {
}
