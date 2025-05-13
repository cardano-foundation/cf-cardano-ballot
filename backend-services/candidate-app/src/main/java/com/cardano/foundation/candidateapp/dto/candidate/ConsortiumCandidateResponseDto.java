package com.cardano.foundation.candidateapp.dto.candidate;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumCandidateResponseDto {
    private CandidateResponseDto candidate;
    private List<ConsortiumMemberResponseDto> members;
}
