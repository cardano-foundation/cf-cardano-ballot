package com.cardano.foundation.candidateapp.mapper.candidate;

import com.cardano.foundation.candidateapp.dto.candidate.ConsortiumMemberRequestDto;
import com.cardano.foundation.candidateapp.dto.candidate.ConsortiumMemberResponseDto;
import com.cardano.foundation.candidateapp.model.candidate.ConsortiumMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsortiumMemberMapper {
    ConsortiumMemberResponseDto toDto(ConsortiumMember entity);
    ConsortiumMember toEntity(ConsortiumMemberRequestDto dto);
}
