package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.CandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CandidateResponseDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividualCandidateServiceTest {

    @Mock CandidateRepository candidateRepository;
    @Mock IndividualCandidateRepository individualRepository;
    @Mock CandidateMapper candidateMapper;
    @Mock IndividualCandidateMapper individualMapper;

    @InjectMocks
    IndividualCandidateService service;

    @Test
    void shouldCreateIndividualCandidate() {
        CandidateRequestDto dto = CandidateRequestDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        CandidateResponseDto responseDto = CandidateResponseDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        Candidate candidateEntity = new Candidate();
        candidateEntity.setId(1L);
        candidateEntity.setCandidateType(CandidateType.individual);

        IndividualCandidate individual = new IndividualCandidate();
        individual.setCandidate(candidateEntity);
        individual.setCandidateId(1L);

        when(candidateMapper.toEntity(dto)).thenReturn(candidateEntity);
        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);
        when(individualRepository.save(any())).thenReturn(individual);
        when(individualMapper.toDto(any())).thenReturn(new IndividualCandidateResponseDto(responseDto));

        IndividualCandidateResponseDto result = service.create(new IndividualCandidateRequestDto(dto), false);

        assertNotNull(result);
        assertEquals("Test User", result.getCandidate().getName());
        verify(candidateRepository).save(any());
        verify(individualRepository).save(any());
    }

    @Test
    void shouldThrowWhenCandidateNotFound() {
        when(individualRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(ResourceNotFoundException.class, () -> service.getById(42L, false));
        assertEquals("Individual candidate not found", ex.getMessage());
    }
}
