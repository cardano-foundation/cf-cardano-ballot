package com.cardano.foundation.candidateapp.mapper.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.CandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.CandidateResponseDto;
import com.cardano.foundation.candidateapp.model.candidate.Candidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    CandidateResponseDto toDto(Candidate entity);
    Candidate toEntity(CandidateRequestDto dto);
}
