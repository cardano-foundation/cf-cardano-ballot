package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposal")
@Getter
@Setter
public class Proposal extends AbstractTimestampEntity {

    @Id
    @Column
    private String id;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "proposal_details_id")
    private ProposalDetails proposalDetails;

}


