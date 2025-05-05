package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.CompanyCandidateMapper;
import com.cardano.foundation.candidateapp.model.Candidate;
import com.cardano.foundation.candidateapp.model.CandidateType;
import com.cardano.foundation.candidateapp.model.CompanyCandidate;
import com.cardano.foundation.candidateapp.repository.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.CompanyCandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyCandidateService {

    private final CandidateRepository candidateRepo;
    private final CompanyCandidateRepository companyRepo;
    private final CandidateMapper candidateMapper;
    private final CompanyCandidateMapper companyMapper;

    public CompanyCandidateResponseDto create(CompanyCandidateRequestDto dto) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setCandidateType(CandidateType.company);
        candidate.setVerified(false);
        Candidate savedCandidate = candidateRepo.save(candidate);

        CompanyCandidate company = new CompanyCandidate();
        company.setCandidate(savedCandidate);
        company.setRegistrationNumber(dto.getRegistrationNumber());
        company.setKeyContactPerson(dto.getKeyContactPerson());

        return companyMapper.toDto(companyRepo.save(company));
    }

    public List<CompanyCandidateResponseDto> getAll() {
        return companyRepo.findAll().stream().map(companyMapper::toDto).collect(Collectors.toList());
    }

    public CompanyCandidateResponseDto getById(Long id) {
        return companyMapper.toDto(
                companyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company candidate not found"))
        );
    }

    public CompanyCandidateResponseDto update(Long id, CompanyCandidateRequestDto dto) {
        CompanyCandidate existing = companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.company);
        Candidate saved = candidateRepo.save(updatedCandidate);

        existing.setCandidate(saved);
        existing.setRegistrationNumber(dto.getRegistrationNumber());
        existing.setKeyContactPerson(dto.getKeyContactPerson());

        return companyMapper.toDto(companyRepo.save(existing));
    }

    public void delete(Long id) {
        candidateRepo.deleteById(id);
    }
}
