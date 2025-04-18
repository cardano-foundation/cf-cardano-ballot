package com.cardano.foundation.candidateapp.dto;

import com.cardano.foundation.candidateapp.model.CandidateType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateRequestDto {
    @NotNull
    private CandidateType candidateType;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String country;

    private String socialX;
    private String socialLinkedin;
    private String socialDiscord;
    private String socialTelegram;
    private String socialOther;

    @NotBlank
    private String publicContact;

    private String about;
    private String bio;
    private String additionalInfo;
    private String videoPresentationLink;

    private String reasonToServe;
    private String governanceExperience;
    private String communicationStrategy;
    private String ecosystemContributions;
    private String legalExpertise;

    @Min(0)
    private Integer weeklyCommitmentHours;

    private String XVerification;
    private String conflictOfInterest;
    private String drepId;
    private String stakeId;
}
