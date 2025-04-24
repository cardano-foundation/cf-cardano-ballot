package com.cardano.foundation.candidateapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCandidate {

    @Id
    private Long candidateId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    private String registrationNumber;
    private String keyContactPerson;
    private String socialWebsite;
}
