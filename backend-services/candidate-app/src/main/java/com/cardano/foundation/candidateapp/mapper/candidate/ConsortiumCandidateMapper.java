package com.cardano.foundation.candidateapp.mapper.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.ConsortiumCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.ConsortiumCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.candidate.ConsortiumCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        CandidateMapper.class
})
public interface ConsortiumCandidateMapper {
    ConsortiumCandidateResponseDto toDto(ConsortiumCandidate entity);
    ConsortiumCandidate toEntity(ConsortiumCandidateRequestDto dto);
}
