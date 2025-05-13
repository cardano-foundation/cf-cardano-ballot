package com.cardano.foundation.candidateapp.service.candidate;

import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.repository.candidate.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyCandidateService {

    private final CandidateRepository candidateRepo;

    public void verify(Long candidateId) {
        var candidateOptional = candidateRepo.findById(candidateId);
        if (candidateOptional.isEmpty()) {
            throw new ResourceNotFoundException("Candidate not found");
        }
        var candidate = candidateOptional.get();
        candidate.setVerified(true);
        candidateRepo.save(candidate);
    }

    public void unverify(Long candidateId) {
        var candidateOptional = candidateRepo.findById(candidateId);
        if (candidateOptional.isEmpty()) {
            throw new ResourceNotFoundException("Candidate not found");
        }
        var candidate = candidateOptional.get();
        candidate.setVerified(false);
        candidateRepo.save(candidate);
    }

}
