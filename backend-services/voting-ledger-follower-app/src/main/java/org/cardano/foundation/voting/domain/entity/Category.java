package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
@Immutable
@IdClass(CategoryId.class)
public class Category extends AbstractTimestampEntity {

    @Column(name = "event_id", nullable = false)
    @Id
    private String eventId;

    @JoinColumn(name = "id", nullable = false)
    @Id
    @ManyToOne
    private String categoryId;

    @Column(name = "gdpr_protection", nullable = false)
    @Builder.Default
    private boolean gdprProtection = true;

    @Column(name = "schema_version", nullable = false)
    private SchemaVersion version;

    @OneToMany(
            mappedBy = "categoryId",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Proposal> proposals =  new ArrayList<>();

    @Column(name = "absolute_slot")
    private long absoluteSlot;

    public boolean isValid() {
        return proposals.size() > 1;
    }

}
