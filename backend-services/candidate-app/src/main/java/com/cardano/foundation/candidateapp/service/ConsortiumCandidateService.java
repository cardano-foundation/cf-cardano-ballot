package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.CandidateMapper;
import com.cardano.foundation.candidateapp.mapper.ConsortiumCandidateMapper;
import com.cardano.foundation.candidateapp.mapper.ConsortiumMemberMapper;
import com.cardano.foundation.candidateapp.model.*;
import com.cardano.foundation.candidateapp.repository.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.ConsortiumCandidateRepository;
import com.cardano.foundation.candidateapp.repository.ConsortiumMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsortiumCandidateService {

    private final CandidateRepository candidateRepo;
    private final ConsortiumCandidateRepository consortiumRepo;
    private final ConsortiumMemberRepository memberRepo;
    private final CandidateMapper candidateMapper;
    private final ConsortiumMemberMapper memberMapper;

    public ConsortiumCandidateResponseDto create(ConsortiumCandidateRequestDto dto) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setCandidateType(CandidateType.consortium);
        candidate.setVerified(false);
        Candidate savedCandidate = candidateRepo.save(candidate);

        ConsortiumCandidate consortium = new ConsortiumCandidate();
        consortium.setCandidate(savedCandidate);
        ConsortiumCandidate savedConsortium = consortiumRepo.save(consortium);

        List<ConsortiumMember> members = dto.getMembers().stream()
                .map(memberDto -> {
                    ConsortiumMember member = memberMapper.toEntity(memberDto);
                    member.setConsortium(savedConsortium);
                    return member;
                }).collect(Collectors.toList());

        List<ConsortiumMember> savedMembers = memberRepo.saveAll(members);
        return ConsortiumCandidateResponseDto.builder()
                .candidate(candidateMapper.toDto(savedConsortium.getCandidate()))
                .members(savedMembers.stream().map(memberMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    public ConsortiumCandidateResponseDto getById(Long id) {
        ConsortiumCandidate consortium = consortiumRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consortium candidate not found"));

        List<ConsortiumMember> members = memberRepo.findByConsortiumCandidateId(id);
        consortium.setCandidate(consortium.getCandidate()); // ensure lazy load

        return ConsortiumCandidateResponseDto.builder()
                .candidate(candidateMapper.toDto(consortium.getCandidate()))
                .members(members.stream().map(memberMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    public List<ConsortiumCandidateResponseDto> getAll() {
        List<ConsortiumCandidate> consortiums = consortiumRepo.findAll();

        return consortiums.stream().map(consortium -> {
            Candidate candidate = consortium.getCandidate();
            List<ConsortiumMember> members = memberRepo.findByConsortiumCandidateId(consortium.getCandidateId());

            return ConsortiumCandidateResponseDto.builder()
                    .candidate(candidateMapper.toDto(candidate))
                    .members(members.stream()
                            .map(memberMapper::toDto)
                            .toList())
                    .build();
        }).toList();
    }

    public ConsortiumCandidateResponseDto update(Long id, ConsortiumCandidateRequestDto dto) {
        ConsortiumCandidate existing = consortiumRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consortium candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.consortium);
        Candidate savedCandidate = candidateRepo.save(updatedCandidate);

        existing.setCandidate(savedCandidate);
        consortiumRepo.save(existing);

        memberRepo.deleteAll(memberRepo.findByConsortiumCandidateId(id));

        List<ConsortiumMember> members = dto.getMembers().stream()
                .map(memberDto -> {
                    ConsortiumMember member = memberMapper.toEntity(memberDto);
                    member.setConsortium(existing);
                    return member;
                }).collect(Collectors.toList());

        memberRepo.saveAll(members);

        return getById(id);
    }

    public void delete(Long id) {
        candidateRepo.deleteById(id);
    }
}
