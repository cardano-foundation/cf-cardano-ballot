package com.cardano.foundation.candidateapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumCandidateRequestDto {
    @Valid
    @NotNull
    private CandidateRequestDto candidate;
    @Valid
    @NotNull
    @Size(min = 2, message = "Consortium must have at least two members")
    private List<ConsortiumMemberRequestDto> members;
}
