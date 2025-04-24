package com.cardano.foundation.candidateapp.mapper;

import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.ConsortiumCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        CandidateMapper.class
})
public interface ConsortiumCandidateMapper {
    ConsortiumCandidateResponseDto toDto(ConsortiumCandidate entity);
    ConsortiumCandidate toEntity(ConsortiumCandidateRequestDto dto);
}
