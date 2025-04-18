package com.cardano.foundation.candidateapp.dto;

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
    private String socialWebsite;
}
