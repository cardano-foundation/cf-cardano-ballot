package com.cardano.foundation.candidateapp.service.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.IndividualCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.IndividualCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.candidate.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.candidate.IndividualCandidateMapper;
import com.cardano.foundation.candidateapp.model.candidate.Candidate;
import com.cardano.foundation.candidateapp.model.candidate.CandidateType;
import com.cardano.foundation.candidateapp.model.candidate.IndividualCandidate;
import com.cardano.foundation.candidateapp.repository.candidate.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.candidate.IndividualCandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualCandidateService {

    private final CandidateRepository candidateRepo;
    private final IndividualCandidateRepository individualRepo;
    private final CandidateMapper candidateMapper;
    private final IndividualCandidateMapper individualMapper;

    public IndividualCandidateResponseDto create(IndividualCandidateRequestDto dto, boolean isDraft) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setVerified(false);
        candidate.setDraft(isDraft);
        candidate.setCandidateType(CandidateType.individual);

        Candidate savedCandidate = candidateRepo.save(candidate);

        IndividualCandidate individual = new IndividualCandidate();
        individual.setCandidate(savedCandidate);

        return individualMapper.toDto(individualRepo.save(individual));
    }

    public List<IndividualCandidateResponseDto> getAll(boolean isDraft) {
        return individualRepo.findAll().stream()
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .map(individualMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<IndividualCandidateResponseDto> getAllByWalletAddress(String walletAddress, boolean isDraft) {
        return individualRepo.findAllByCandidate_WalletAddress(walletAddress).stream()
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .map(individualMapper::toDto)
                .collect(Collectors.toList());
    }

    public IndividualCandidateResponseDto getById(Long id, boolean isDraft) {
        IndividualCandidate entity = individualRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));
        return individualMapper.toDto(entity);
    }

    public IndividualCandidateResponseDto update(Long id, IndividualCandidateRequestDto dto, boolean isDraft) {
        IndividualCandidate existing = individualRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.individual);

        Candidate saved = candidateRepo.save(updatedCandidate);

        existing.setCandidate(saved);
        return individualMapper.toDto(individualRepo.save(existing));
    }

    public IndividualCandidateResponseDto publish(Long id, IndividualCandidateRequestDto dto) {
        IndividualCandidate existing = individualRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft())
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.individual);
        updatedCandidate.setDraft(false);

        Candidate saved = candidateRepo.save(updatedCandidate);

        existing.setCandidate(saved);
        return individualMapper.toDto(individualRepo.save(existing));
    }

    public void delete(Long id, boolean isDraft) {
        individualRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));
        candidateRepo.deleteById(id);
    }
}
