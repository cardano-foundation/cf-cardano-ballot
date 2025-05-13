package com.cardano.foundation.candidateapp.mapper.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.IndividualCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.IndividualCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.candidate.IndividualCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CandidateMapper.class })
public interface IndividualCandidateMapper {
    IndividualCandidateResponseDto toDto(IndividualCandidate entity);
    IndividualCandidate toEntity(IndividualCandidateRequestDto dto);
}
