package com.cardano.foundation.candidateapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumMemberRequestDto {
    private String name;
    private String country;
    private String bio;

    private String socialX;
    private String socialLinkedin;
    private String socialDiscord;
    private String socialTelegram;
    private String socialOther;

    private String XVerification;
    private String conflictOfInterest;
    private String drepId;
    private String stakeId;
}
