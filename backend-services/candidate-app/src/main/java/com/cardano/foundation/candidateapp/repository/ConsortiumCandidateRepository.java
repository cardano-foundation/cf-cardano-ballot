package com.cardano.foundation.candidateapp.repository;

import com.cardano.foundation.candidateapp.model.ConsortiumCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsortiumCandidateRepository extends JpaRepository<ConsortiumCandidate, Long> {
    List<ConsortiumCandidate> findAllByCandidate_WalletAddress(String walletAddress);
}
