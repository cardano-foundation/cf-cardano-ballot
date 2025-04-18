package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.CompanyCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyCandidateRepository extends JpaRepository<CompanyCandidate, Long> {
}
