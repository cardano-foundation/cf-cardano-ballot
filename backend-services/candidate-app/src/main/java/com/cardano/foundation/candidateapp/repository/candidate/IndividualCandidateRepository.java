package com.cardano.foundation.candidateapp.repository.candidate;

import com.cardano.foundation.candidateapp.model.candidate.IndividualCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualCandidateRepository extends JpaRepository<IndividualCandidate, Long> {
    List<IndividualCandidate> findAllByCandidate_WalletAddress(String walletAddress);
}
