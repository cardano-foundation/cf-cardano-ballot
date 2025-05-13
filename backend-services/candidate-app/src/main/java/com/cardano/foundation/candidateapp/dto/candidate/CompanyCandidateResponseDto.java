package com.cardano.foundation.candidateapp.dto.candidate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCandidateResponseDto {
    private CandidateResponseDto candidate;
    private String registrationNumber;
    private String keyContactPerson;
}
