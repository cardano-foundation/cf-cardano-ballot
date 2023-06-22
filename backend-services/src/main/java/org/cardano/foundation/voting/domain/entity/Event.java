package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.EventType;

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

    @Id
    @Column(nullable = false)
    private String id; // e.g. 90ed2df9-dd21-4567-90e2-e8f09b9c422c

    @Column(nullable = false)
    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Column(nullable = false)
    private String name; // e.g. Voltaire_Pre_Ratification

    @Column(nullable = false)
    private String presentationName; // e.g. Voltaire Pre-Ratification

    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column
    @Nullable
    private String description;

    @Column(name = "start_epoch")
    @Nullable
    private int startEpoch;

    @Column(name = "end_epoch")
    @Nullable
    private int endEpoch;

    @Column(name = "start_slot")
    @Nullable
    private int startSlot;

    @Column(name = "end_slot")
    @Nullable
    private int endSlot;

    @Column(name = "snapshot_epoch")
    @Nullable
    private int snapshotEpoch;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    public Optional<Category> findCategoryByName(String categoryName) {
        return categories.stream().filter(category -> category.getName().equals(categoryName)).findFirst();
    }

    public Optional<Proposal> findProposal(String categoryId, String proposalId) {
        return categories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst().flatMap(category -> category.getProposals().stream()
                .filter(proposal -> proposal.getId().equals(proposalId))
                .findFirst());
    }

}
