package com.cardano.foundation.candidateapp.mapper;

import com.cardano.foundation.candidateapp.dto.IndividualCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.IndividualCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.IndividualCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CandidateMapper.class })
public interface IndividualCandidateMapper {
    IndividualCandidateResponseDto toDto(IndividualCandidate entity);
    IndividualCandidate toEntity(IndividualCandidateRequestDto dto);
}
