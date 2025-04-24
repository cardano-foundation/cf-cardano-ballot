package com.cardano.foundation.candidateapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCandidateRequestDto {
    @Valid
    @NotNull
    private CandidateRequestDto candidate;
    private String registrationNumber;
    private String keyContactPerson;
    private String socialWebsite;
}
