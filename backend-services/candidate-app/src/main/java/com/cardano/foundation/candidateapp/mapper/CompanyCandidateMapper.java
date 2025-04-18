package com.cardano.foundation.candidateapp.mapper;

import com.cardano.foundation.candidateapp.dto.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.CompanyCandidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CandidateMapper.class })
public interface CompanyCandidateMapper {
    CompanyCandidateResponseDto toDto(CompanyCandidate entity);
    CompanyCandidate toEntity(CompanyCandidateRequestDto dto);
}
