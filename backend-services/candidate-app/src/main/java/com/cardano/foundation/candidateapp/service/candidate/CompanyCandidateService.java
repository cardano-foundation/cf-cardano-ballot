package com.cardano.foundation.candidateapp.service.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.candidate.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.candidate.CompanyCandidateMapper;
import com.cardano.foundation.candidateapp.model.candidate.Candidate;
import com.cardano.foundation.candidateapp.model.candidate.CandidateType;
import com.cardano.foundation.candidateapp.model.candidate.CompanyCandidate;
import com.cardano.foundation.candidateapp.repository.candidate.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.candidate.CompanyCandidateRepository;
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

    public CompanyCandidateResponseDto create(CompanyCandidateRequestDto dto, boolean isDraft) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setCandidateType(CandidateType.company);
        candidate.setVerified(false);
        candidate.setDraft(isDraft);
        Candidate savedCandidate = candidateRepo.save(candidate);

        CompanyCandidate company = new CompanyCandidate();
        company.setCandidate(savedCandidate);
        company.setRegistrationNumber(dto.getRegistrationNumber());
        company.setKeyContactPerson(dto.getKeyContactPerson());

        return companyMapper.toDto(companyRepo.save(company));
    }

    public List<CompanyCandidateResponseDto> getAll(boolean isDraft) {
        return companyRepo.findAll().stream()
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CompanyCandidateResponseDto> getAllByWalletAddress(String walletAddress, boolean isDraft) {
        return companyRepo.findAllByCandidate_WalletAddress(walletAddress).stream()
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    public CompanyCandidateResponseDto getById(Long id, boolean isDraft) {
        return companyMapper.toDto(companyRepo.findById(id)
                        .filter(e -> e.getCandidate().isDraft() == isDraft)
                        .orElseThrow(() -> new ResourceNotFoundException("Company candidate not found"))
        );
    }

    public CompanyCandidateResponseDto update(Long id, CompanyCandidateRequestDto dto, boolean isDraft) {
        CompanyCandidate existing = companyRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
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

    public CompanyCandidateResponseDto publish(Long id, CompanyCandidateRequestDto dto) {
        CompanyCandidate existing = companyRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft())
                .orElseThrow(() -> new ResourceNotFoundException("Company candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.company);
        updatedCandidate.setDraft(false);
        Candidate saved = candidateRepo.save(updatedCandidate);

        existing.setCandidate(saved);
        existing.setRegistrationNumber(dto.getRegistrationNumber());
        existing.setKeyContactPerson(dto.getKeyContactPerson());

        return companyMapper.toDto(companyRepo.save(existing));
    }

    public void delete(Long id, boolean isDraft) {
        companyRepo.findById(id)
            .filter(e -> e.getCandidate().isDraft() == isDraft)
            .orElseThrow(() -> new ResourceNotFoundException("Company candidate not found"));
        candidateRepo.deleteById(id);
    }
}
