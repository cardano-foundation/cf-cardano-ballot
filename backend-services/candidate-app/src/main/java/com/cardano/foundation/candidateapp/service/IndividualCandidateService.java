package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.IndividualCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.IndividualCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.IndividualCandidateMapper;
import com.cardano.foundation.candidateapp.model.Candidate;
import com.cardano.foundation.candidateapp.model.CandidateType;
import com.cardano.foundation.candidateapp.model.IndividualCandidate;
import com.cardano.foundation.candidateapp.repository.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.IndividualCandidateRepository;
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

    public IndividualCandidateResponseDto create(IndividualCandidateRequestDto dto) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setCandidateType(CandidateType.individual);

        Candidate savedCandidate = candidateRepo.save(candidate);

        IndividualCandidate individual = new IndividualCandidate();
        individual.setCandidate(savedCandidate);

        return individualMapper.toDto(individualRepo.save(individual));
    }

    public IndividualCandidateResponseDto getById(Long id) {
        IndividualCandidate entity = individualRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));
        return individualMapper.toDto(entity);
    }

    public List<IndividualCandidateResponseDto> getAll() {
        return individualRepo.findAll().stream().map(individualMapper::toDto).collect(Collectors.toList());
    }

    public IndividualCandidateResponseDto update(Long id, IndividualCandidateRequestDto dto) {
        IndividualCandidate existing = individualRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.individual);

        Candidate saved = candidateRepo.save(updatedCandidate);

        existing.setCandidate(saved);
        return individualMapper.toDto(individualRepo.save(existing));
    }

    public void delete(Long id) {
        candidateRepo.deleteById(id);
    }
}
