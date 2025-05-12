package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.candidate.*;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.candidate.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.candidate.ConsortiumMemberMapper;
import com.cardano.foundation.candidateapp.model.candidate.Candidate;
import com.cardano.foundation.candidateapp.model.candidate.ConsortiumCandidate;
import com.cardano.foundation.candidateapp.model.candidate.ConsortiumMember;
import com.cardano.foundation.candidateapp.repository.candidate.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.candidate.ConsortiumCandidateRepository;
import com.cardano.foundation.candidateapp.repository.candidate.ConsortiumMemberRepository;
import com.cardano.foundation.candidateapp.service.candidate.ConsortiumCandidateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsortiumCandidateServiceTest {
    @Mock
    CandidateRepository candidateRepo;
    @Mock
    ConsortiumCandidateRepository consortiumRepo;
    @Mock
    ConsortiumMemberRepository memberRepo;
    @Mock
    CandidateMapper candidateMapper;
    @Mock
    ConsortiumMemberMapper memberMapper;

    @InjectMocks
    ConsortiumCandidateService service;

    @Test
    void shouldCreateConsortiumCandidateWithMembers() {
        CandidateRequestDto candidateDto = CandidateRequestDto.builder()
                .name("DAO United")
                .email("dao@united.io")
                .build();
        CandidateResponseDto responseCandidateDto = CandidateResponseDto.builder()
                .name("DAO United")
                .email("dao@united.io")
                .build();

        ConsortiumMemberRequestDto memberDto = ConsortiumMemberRequestDto.builder()
                .name("Alice")
                .country("Germany")
                .build();
        ConsortiumMemberResponseDto responseMemberDto = ConsortiumMemberResponseDto.builder()
                .name("Alice")
                .country("Germany")
                .build();

        ConsortiumCandidateRequestDto input = ConsortiumCandidateRequestDto.builder()
                .candidate(candidateDto)
                .members(List.of(memberDto))
                .build();

        Candidate entity = new Candidate();
        entity.setId(1L);

        ConsortiumCandidate savedConsortium = new ConsortiumCandidate();
        savedConsortium.setCandidate(entity);

        when(candidateMapper.toEntity(candidateDto)).thenReturn(entity);
        when(candidateRepo.save(entity)).thenReturn(entity);
        when(consortiumRepo.save(any())).thenReturn(savedConsortium);
        when(memberMapper.toEntity(memberDto)).thenReturn(new ConsortiumMember());

        ConsortiumCandidateResponseDto result = service.create(input, false);

        assertNotNull(result);
        verify(candidateRepo).save(entity);
        verify(memberRepo).saveAll(any());
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(consortiumRepo.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(ResourceNotFoundException.class, () -> service.getById(42L, false));
        assertEquals("Consortium candidate not found", ex.getMessage());
    }
}
