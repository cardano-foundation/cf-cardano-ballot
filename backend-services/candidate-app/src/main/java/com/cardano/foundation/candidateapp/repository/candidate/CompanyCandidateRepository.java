package com.cardano.foundation.candidateapp.repository.candidate;

import com.cardano.foundation.candidateapp.model.candidate.CompanyCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyCandidateRepository extends JpaRepository<CompanyCandidate, Long> {
    List<CompanyCandidate> findAllByCandidate_WalletAddress(String walletAddress);
}
