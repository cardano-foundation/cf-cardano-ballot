package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.cardano.foundation.voting.domain.SnapshotEpochType;

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
@ToString
public class Event extends AbstractTimestampEntity {

    @Id
    @Column
    private String id; // e.g. 90ed2df9-dd21-4567-90e2-e8f09b9c422c

    @Column
    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Column
    private String name; // e.g. Voltaire_Pre_Ratification

    @Column
    private String presentationName; // e.g. Voltaire Pre-Ratification

    @Column
    @Nullable
    private String description;

    @Column(name = "start_slot")
    @NotNull
    private int startSlot;

    @Column(name = "end_slot")
    @NotNull
    private int endSlot;

    @Column(name = "snapshot_epoch")
    @NotNull
    private int snapshotEpoch;

    @Column(name = "snapshot_epoch_type")
    @NotNull
    @Builder.Default
    private SnapshotEpochType snapshotEpochType = SnapshotEpochType.EPOCH_END;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    public Optional<Category> getCategory(String categoryId) {
        return categories.stream().filter(category -> category.getId().equals(categoryId)).findFirst();
    }

    public Optional<Proposal> getProposal(String categoryId, String proposalId) {
        return categories.stream().filter(category -> category.getId().equals(categoryId)).findFirst().flatMap(category -> category.getProposals().stream().filter(proposal -> proposal.getId().equals(proposalId)).findFirst());
    }

    public boolean isActive(int currentSlot) {
        return currentSlot >= startSlot && currentSlot <= endSlot;
    }

}
