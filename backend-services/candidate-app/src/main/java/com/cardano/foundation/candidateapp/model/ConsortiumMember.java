package com.cardano.foundation.candidateapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consortium_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "consortium_id")
    private ConsortiumCandidate consortium;

    private String name;
    private String country;
    private String bio;

    @Column(name = "social_x")
    private String socialX;
    private String socialLinkedin;
    private String socialDiscord;
    private String socialTelegram;
    private String socialOther;
    private String socialWebsite;

    @Column(name = "x_verification")
    private String XVerification;
    private String conflictOfInterest;
    private String drepId;
    private String stakeId;
}
