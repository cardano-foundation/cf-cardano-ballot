package com.cardano.foundation.candidateapp.mapper;

import com.cardano.foundation.candidateapp.dto.ConsortiumMemberRequestDto;
import com.cardano.foundation.candidateapp.dto.ConsortiumMemberResponseDto;
import com.cardano.foundation.candidateapp.model.ConsortiumMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsortiumMemberMapper {
    ConsortiumMemberResponseDto toDto(ConsortiumMember entity);
    ConsortiumMember toEntity(ConsortiumMemberRequestDto dto);
}
