package com.cardano.foundation.candidateapp.mapper;

import com.cardano.foundation.candidateapp.dto.CandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CandidateResponseDto;
import com.cardano.foundation.candidateapp.model.Candidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    CandidateResponseDto toDto(Candidate entity);
    Candidate toEntity(CandidateRequestDto dto);
}
