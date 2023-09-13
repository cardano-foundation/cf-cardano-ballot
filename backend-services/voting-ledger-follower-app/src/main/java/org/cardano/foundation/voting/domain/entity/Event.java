package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.hibernate.annotations.Immutable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;

@Entity
@Table(name = "event")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Immutable
public class Event extends AbstractTimestampEntity {

    @Getter
    @Setter
    @Column(nullable = false)
    @Id
    private String id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String organisers; // e.g. CF

    @Column(name = "event_type", nullable = false)
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private VotingEventType votingEventType;

    @Column(name = "voting_power_asset")
    // voting power asset is only needed for stake based voting events
    @Nullable
    @Enumerated(EnumType.STRING)
    private VotingPowerAsset votingPowerAsset;

    @Column(name = "allow_vote_changing")
    @Nullable
    @Builder.Default
    private Boolean allowVoteChanging = false;

    @Column(name = "high_level_epoch_results_while_voting")
    @Nullable
    @Builder.Default
    private Boolean highLevelEpochResultsWhileVoting = false;

    @Column(name = "high_level_category_results_while_voting")
    @Nullable
    @Builder.Default
    private Boolean highLevelCategoryResultsWhileVoting = false;

    @Column(name = "category_results_while_voting")
    @Nullable
    @Builder.Default
    private Boolean categoryResultsWhileVoting = false;

    @Column(name = "start_epoch")
    // startEpoch is only needed for stake based voting events
    @Nullable
    private Integer startEpoch;

    @Column(name = "end_epoch")
    // endEpoch is only needed for stake based voting events
    @Nullable
    private Integer endEpoch;

    @Column(name = "snapshot_epoch")
    // snapshotEpoch is only needed for stake based voting events
    @Nullable
    private Integer snapshotEpoch;

    @Column(name = "proposals_reveal_epoch")
    // proposalsRevealEpoch is only needed for stake based voting events
    @Nullable
    private Integer proposalsRevealEpoch;

    @Column(name = "start_slot")
    //startSlot is only needed for user based voting events
    @Nullable
    private Long startSlot;

    @Column(name = "end_slot")
    // endSlot is only needed for user based voting events
    @Nullable
    private Long endSlot;

    @Column(name = "proposals_reveal_slot")
    // proposalsRevealSlot is only needed for user based voting events
    @Nullable
    private Long proposalsRevealSlot;

    @Column(name = "schema_version")
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private SchemaVersion version;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Builder.Default
    @Getter
    @Setter
    private List<Category> categories = new ArrayList<>();

    @Column(name = "absolute_slot")
    @Getter
    @Setter
    private long absoluteSlot;

    public Optional<Category> findCategoryByName(String categoryName) {
        return categories.stream().filter(category -> category.getId().equals(categoryName)).findFirst();
    }

    public Optional<VotingPowerAsset> getVotingPowerAsset() {
        return Optional.ofNullable(votingPowerAsset);
    }

    public void setVotingPowerAsset(Optional<VotingPowerAsset> votingPowerAsset) {
        this.votingPowerAsset = votingPowerAsset.orElse(null);
    }

    public boolean isAllowVoteChanging() {
        return Optional.ofNullable(allowVoteChanging).orElse(false);
    }

    public void setAllowVoteChanging(boolean allowVoteChanging) {
        this.allowVoteChanging = allowVoteChanging;
    }

    public Optional<Integer> getStartEpoch() {
        return Optional.ofNullable(startEpoch);
    }

    public void setStartEpoch(Optional<Integer> startEpoch) {
        this.startEpoch = startEpoch.orElse(null);
    }

    public Optional<Integer> getEndEpoch() {
        return Optional.ofNullable(endEpoch);
    }

    public void setEndEpoch(Optional<Integer> endEpoch) {
        this.endEpoch = endEpoch.orElse(null);
    }

    public Optional<Long> getStartSlot() {
        return Optional.ofNullable(startSlot);
    }

    public void setStartSlot(Optional<Long> startSlot) {
        this.startSlot = startSlot.orElse(null);
    }

    public Optional<Long> getEndSlot() {
        return Optional.ofNullable(endSlot);
    }

    public void setEndSlot(Optional<Long> endSlot) {
        this.endSlot = endSlot.orElse(null);
    }

    public Optional<Integer> getSnapshotEpoch() {
        return Optional.ofNullable(snapshotEpoch);
    }

    public void setSnapshotEpoch(Optional<Integer> snapshotEpoch) {
        this.snapshotEpoch = snapshotEpoch.orElse(null);
    }

    public Optional<Boolean> getHighLevelEpochResultsWhileVoting() {
        return Optional.ofNullable(highLevelEpochResultsWhileVoting);
    }

    public void setHighLevelEpochResultsWhileVoting(Optional<Boolean> highLevelEpochResultsWhileVoting) {
        this.highLevelEpochResultsWhileVoting = highLevelEpochResultsWhileVoting.orElse(null);
    }

    public Optional<Boolean> getHighLevelCategoryResultsWhileVoting() {
        return Optional.ofNullable(highLevelCategoryResultsWhileVoting);
    }

    public void setHighLevelCategoryResultsWhileVoting(Optional<Boolean> highLevelCategoryResultsWhileVoting) {
        this.highLevelCategoryResultsWhileVoting = highLevelCategoryResultsWhileVoting.orElse(null);
    }

    public Optional<Boolean> getCategoryResultsWhileVoting() {
        return Optional.ofNullable(categoryResultsWhileVoting);
    }

    public void setCategoryResultsWhileVoting(Optional<Boolean> categoryResultsWhileVoting) {
        this.categoryResultsWhileVoting = categoryResultsWhileVoting.orElse(null);
    }

    public Optional<Integer> getProposalsRevealEpoch() {
        return Optional.ofNullable(proposalsRevealEpoch);
    }

    public void setProposalsRevealEpoch(Optional<Integer> proposalsRevealEpoch) {
        this.proposalsRevealEpoch = proposalsRevealEpoch.orElse(null);
    }

    public Optional<Long> getProposalsRevealSlot() {
        return Optional.ofNullable(proposalsRevealSlot);
    }

    public void setProposalsRevealSlot(Optional<Long> proposalsRevealSlot) {
        this.proposalsRevealSlot = proposalsRevealSlot.orElse(null);
    }

    public boolean isValid() {
        if (List.of(STAKE_BASED, BALANCE_BASED).contains(votingEventType)) {
            if (getStartEpoch().isEmpty() || getEndEpoch().isEmpty() || getSnapshotEpoch().isEmpty() || getVotingPowerAsset().isEmpty() || getProposalsRevealEpoch().isEmpty()) {
                return false;
            }
        }
        if (votingEventType == VotingEventType.USER_BASED) {
            if (getStartSlot().isEmpty() || getEndSlot().isEmpty() || getProposalsRevealSlot().isEmpty()) {
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
