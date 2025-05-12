package com.cardano.foundation.candidateapp.mapper.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.candidate.CompanyCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CandidateMapper.class })
public interface CompanyCandidateMapper {
    CompanyCandidateResponseDto toDto(CompanyCandidate entity);
    CompanyCandidate toEntity(CompanyCandidateRequestDto dto);
}
