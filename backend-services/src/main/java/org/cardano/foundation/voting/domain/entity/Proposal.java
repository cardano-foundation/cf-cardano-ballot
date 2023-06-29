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

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "category_id")
    private Category category;

}


