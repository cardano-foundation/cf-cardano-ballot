package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposal")
@Getter
@Setter
@ToString
public class Proposal extends AbstractTimestampEntity {

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private String presentationName;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    @Nullable
    private String description;

}
