package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @Column(name = "start_epoch")
    @NotNull
    private int startEpoch;

    @Column(name = "end_epoch")
    @NotNull
    private int endEpoch;

    @Column(name = "snapshot_epoch")
    @NotNull
    private int snapshotEpoch;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    public Optional<Category> findCategory(String categoryId) {
        return categories.stream().filter(category -> category.getId().equals(categoryId)).findFirst();
    }

    public Optional<Proposal> findProposal(String categoryId, String proposalId) {
        return categories.stream().filter(category -> category.getId().equals(categoryId)).findFirst().flatMap(category -> category.getProposals().stream().filter(proposal -> proposal.getId().equals(proposalId)).findFirst());
    }

    public boolean isActive(long epochNo) {
        return epochNo >= startEpoch && epochNo <= endEpoch;
    }

    public boolean isInactive(long epochNo) {
        return !isActive(epochNo);
    }

}
