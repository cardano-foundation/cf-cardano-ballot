package com.cardano.foundation.candidateapp.service;

import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.CandidateMapper;
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

    public ConsortiumCandidateResponseDto create(ConsortiumCandidateRequestDto dto, boolean isDraft) {
        Candidate candidate = candidateMapper.toEntity(dto.getCandidate());
        candidate.setCandidateType(CandidateType.consortium);
        candidate.setVerified(false);
        candidate.setDraft(isDraft);
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

    public List<ConsortiumCandidateResponseDto> getAll(boolean isDraft) {
        List<ConsortiumCandidate> consortiums = consortiumRepo.findAll();
        return mapToDto(consortiums, isDraft);
    }

    public List<ConsortiumCandidateResponseDto> getAllByWalletAddress(String walletAddress, boolean isDraft) {
        List<ConsortiumCandidate> consortiums = consortiumRepo.findAllByCandidate_WalletAddress(walletAddress);
        return mapToDto(consortiums, isDraft);
    }

    public ConsortiumCandidateResponseDto getById(Long id, boolean isDraft) {
        ConsortiumCandidate consortium = consortiumRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Consortium candidate not found"));

        List<ConsortiumMember> members = memberRepo.findByConsortiumCandidateId(id);
        consortium.setCandidate(consortium.getCandidate()); // ensure lazy load

        return ConsortiumCandidateResponseDto.builder()
                .candidate(candidateMapper.toDto(consortium.getCandidate()))
                .members(members.stream().map(memberMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    public ConsortiumCandidateResponseDto update(Long id, ConsortiumCandidateRequestDto dto, boolean isDraft) {
        return saveOrPublish(id, dto, isDraft, isDraft);
    }

    public ConsortiumCandidateResponseDto publish(Long id, ConsortiumCandidateRequestDto dto) {
        return saveOrPublish(id, dto, true, false);
    }

    public void delete(Long id, boolean isDraft) {
        consortiumRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Consortium candidate not found"));
        candidateRepo.deleteById(id);
    }

    private List<ConsortiumCandidateResponseDto> mapToDto(List<ConsortiumCandidate> consortiums, boolean isDraft) {
        return consortiums.stream()
                .filter(e -> e.getCandidate().isDraft() == isDraft)
                .map(consortium -> {
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

    private ConsortiumCandidateResponseDto saveOrPublish(Long id, ConsortiumCandidateRequestDto dto, boolean lookupDraft, boolean saveAsDraft) {
        ConsortiumCandidate existing = consortiumRepo.findById(id)
                .filter(e -> e.getCandidate().isDraft() == lookupDraft)
                .orElseThrow(() -> new ResourceNotFoundException("Consortium candidate not found"));

        Candidate updatedCandidate = candidateMapper.toEntity(dto.getCandidate());
        updatedCandidate.setId(existing.getCandidate().getId());
        updatedCandidate.setCandidateType(CandidateType.consortium);
        updatedCandidate.setDraft(saveAsDraft);
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

        return getById(id, saveAsDraft);
    }
}
