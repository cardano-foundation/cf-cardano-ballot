package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.CandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CandidateResponseDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyCandidateServiceTest {

    @Mock CandidateRepository candidateRepo;
    @Mock CompanyCandidateRepository companyRepo;
    @Mock CandidateMapper candidateMapper;
    @Mock CompanyCandidateMapper companyMapper;

    @InjectMocks
    CompanyCandidateService service;

    @Test
    void shouldCreateCompanyCandidate() {
        CandidateRequestDto dto = CandidateRequestDto.builder()
                .name("Acme Inc.")
                .email("contact@acme.com")
                .build();
        CandidateResponseDto responseDto = CandidateResponseDto.builder()
                .name("Acme Inc.")
                .email("contact@acme.com")
                .build();

        Candidate entity = new Candidate();
        entity.setId(1L);

        CompanyCandidate company = new CompanyCandidate();
        company.setCandidate(entity);
        company.setRegistrationNumber("REG-123");
        company.setKeyContactPerson("John Doe");

        when(candidateMapper.toEntity(dto)).thenReturn(entity);
        when(candidateRepo.save(any())).thenReturn(entity);
        when(companyRepo.save(any())).thenReturn(company);
        when(companyMapper.toDto(any())).thenReturn(new CompanyCandidateResponseDto(responseDto, "REG-123", "John Doe"));

        CompanyCandidateResponseDto result = service.create(new CompanyCandidateRequestDto(dto, "REG-123", "John Doe"), false);

        assertEquals("Acme Inc.", result.getCandidate().getName());
        verify(candidateRepo).save(any());
        verify(companyRepo).save(any());
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(companyRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(ResourceNotFoundException.class, () -> service.getById(999L, false));
        assertEquals("Company candidate not found", ex.getMessage());
    }
}
