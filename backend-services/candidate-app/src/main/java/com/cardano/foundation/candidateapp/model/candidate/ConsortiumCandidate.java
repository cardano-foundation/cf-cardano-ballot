package com.cardano.foundation.candidateapp.model.candidate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consortium_candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsortiumCandidate {

    @Id
    private Long candidateId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
}
