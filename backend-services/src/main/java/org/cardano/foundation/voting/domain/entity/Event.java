package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.EventType;
import org.cardano.foundation.voting.domain.SchemaVersion;

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
    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Column(nullable = false)
    private String presentationName; // e.g. Voltaire Pre-Ratification

    @Column(name = "gdpr_protection", nullable = false)
    @Builder.Default
    private boolean gdprProtection = true; // GDPR protection is enabled by default and it protects proposal ids not being stored on chain

    @Column(name = "event_type", nullable = false)
    private EventType eventType;

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

    @Column(name = "l1_transaction_hash")
    @Nullable
    private String l1TransactionHash;

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
