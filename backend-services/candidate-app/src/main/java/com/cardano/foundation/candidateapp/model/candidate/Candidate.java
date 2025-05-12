package com.cardano.foundation.candidateapp.model.candidate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "candidate_type", nullable = false)
    private CandidateType candidateType;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private IndividualCandidate individualCandidate;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CompanyCandidate companyCandidate;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ConsortiumCandidate consortiumCandidate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;
    private String country;

    private String coldCredentials;
    private String governanceActionRationale;

    @Column(name = "social_x")
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

    @Column(name = "x_verification")
    private String XVerification;
    private String conflictOfInterest;
    private String drepId;
    private String stakeId;

    private String walletAddress;
    @Column(name = "is_verified")
    private boolean verified;

    @Column(name = "is_draft")
    private boolean draft;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
