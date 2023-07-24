package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;

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

    @Column(name = "voting_power_asset")
    @Nullable
    private VotingPowerAsset votingPowerAsset; // voting power asset is only needed for stake based voting events

    @Builder.Default
    @Column(name = "allow_vote_changing")
    private boolean allowVoteChanging = false;

    @Builder.Default
    @Column(name = "category_results_while_voting")
    private boolean categoryResultsWhileVoting = false;

    @Column(name = "start_epoch")
    @Nullable // startEpoch is only needed for stake based voting events
    private Integer startEpoch;

    @Column(name = "end_epoch")
    @Nullable // endEpoch is only needed for stake based voting events
    private Integer endEpoch;

    @Column(name = "start_slot")
    @Nullable // startSlot is only needed for user based voting events
    private Long startSlot;

    @Column(name = "end_slot")
    @Nullable // endSlot is only needed for user based voting events
    private Long endSlot;

    @Column(name = "snapshot_epoch")
    @Nullable // snapshotEpoch is only needed for stake based voting events
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
        if (List.of(STAKE_BASED, BALANCE_BASED).contains(votingEventType)) {
            if (startEpoch == null || endEpoch == null || snapshotEpoch == null || votingPowerAsset == null) {
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
