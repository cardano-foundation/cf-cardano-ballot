package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.VotingEventType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "event")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Event extends AbstractTimestampEntity {

    @Column(nullable = false)
    @Id
    private String id; // e.g. Voltaire_Pre_Ratification

    @Column(nullable = false)
    private String team; // e.g. CF Team

    @Column(name = "event_type", nullable = false)
    private VotingEventType votingEventType;

    @Column(name = "start_epoch")
    @Nullable
    private Integer startEpoch;

    @Column(name = "end_epoch")
    @Nullable
    private Integer endEpoch;

    @Column(name = "start_slot")
    @Nullable
    private Long startSlot;

    @Column(name = "end_slot")
    @Nullable
    private Long endSlot;

    @Column(name = "snapshot_epoch")
    @Nullable
    private Integer snapshotEpoch;

    @Column(name = "schema_version")
    private SchemaVersion version;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    public Optional<Category> findCategoryByName(String categoryName) {
        return categories.stream().filter(category -> category.getId().equals(categoryName)).findFirst();
    }

}
