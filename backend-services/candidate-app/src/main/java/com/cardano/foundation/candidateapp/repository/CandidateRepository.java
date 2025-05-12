package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findAllByWalletAddress(String walletAddress);
}
