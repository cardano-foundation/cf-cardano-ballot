package com.cardano.foundation.candidateapp.dto.candidate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualCandidateResponseDto {
    private CandidateResponseDto candidate;
}
