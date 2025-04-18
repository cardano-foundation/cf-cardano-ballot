package com.cardano.foundation.candidateapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "individual_candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndividualCandidate {

    @Id
    private Long candidateId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
}
