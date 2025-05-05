package com.cardano.foundation.candidateapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    private String country;

    private String coldCredentials;
    private String governanceActionRationale;

    private String socialX;
    private String socialLinkedin;
    private String socialDiscord;
    private String socialTelegram;
    private String socialOther;
    private String socialWebsite;

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

    private String weeklyCommitmentHours;

    private String XVerification;
    private String conflictOfInterest;
    private String drepId;
    private String stakeId;

    @NotBlank
    private String walletAddress;
}
