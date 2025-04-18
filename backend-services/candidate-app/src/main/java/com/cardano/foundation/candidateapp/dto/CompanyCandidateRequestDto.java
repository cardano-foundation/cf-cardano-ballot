package com.cardano.foundation.candidateapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String registrationNumber;
    @NotBlank
    private String keyContactPerson;
    private String socialWebsite;
}
