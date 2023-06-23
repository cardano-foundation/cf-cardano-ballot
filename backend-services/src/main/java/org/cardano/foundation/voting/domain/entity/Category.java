package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.SchemaVersion;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category extends AbstractTimestampEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "presentation_name")
    private String presentationName;

    @Column(name = "l1_transaction_hash")
    private String l1TransactionHash;

    @Column(name = "schema_version")
    private SchemaVersion version;


    @ManyToOne(
            fetch = FetchType.EAGER, cascade = CascadeType.ALL
    )
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Proposal> proposals =  new ArrayList<>();

}
