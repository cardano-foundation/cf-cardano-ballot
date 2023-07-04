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

    @Builder.Default
    @Column(name = "allow_vote_changing")
    private boolean allowVoteChanging = false;

    @Builder.Default
    @Column(name = "category_results_while_voting")
    private boolean categoryResultsWhileVoting = false;

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

    public boolean isValid() {
        if (votingEventType == VotingEventType.STAKE_BASED) {
            if (startEpoch == null || endEpoch == null || snapshotEpoch == null) {
                return false;
            }
        }
        if (votingEventType == VotingEventType.USER_BASED) {
            if (startSlot == null || endSlot == null) {
                return false;
            }
        }
        if (categories.isEmpty()) {
            return false;
        }
        var anyCategoryInvalid = categories.stream().anyMatch(category -> !category.isValid());

        if (anyCategoryInvalid) {
            return false;
        }

        return true;
    }


}
