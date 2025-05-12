package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.IndividualCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualCandidateRepository extends JpaRepository<IndividualCandidate, Long> {
    List<IndividualCandidate> findAllByCandidate_WalletAddress(String walletAddress);
}
