package com.cardano.foundation.candidateapp.dto.candidate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualCandidateRequestDto {
    @Valid
    @NotNull
    private CandidateRequestDto candidate;
}
