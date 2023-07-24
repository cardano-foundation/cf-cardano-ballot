package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
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
    // voting power asset is only needed for stake based voting events
    @Nullable
    private VotingPowerAsset votingPowerAsset;

    @Column(name = "allow_vote_changing")
    @Nullable
    @Builder.Default
    private Boolean allowVoteChanging = false;

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
    private Integer endEpoch;

    @Column(name = "start_slot")
    //startSlot is only needed for user based voting events
    private Long startSlot;

    @Column(name = "end_slot")
    // endSlot is only needed for user based voting events
    @Nullable
    private Long endSlot;

    @Column(name = "snapshot_epoch")
    // snapshotEpoch is only needed for stake based voting events
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public VotingEventType getVotingEventType() {
        return votingEventType;
    }

    public void setVotingEventType(VotingEventType votingEventType) {
        this.votingEventType = votingEventType;
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

    public boolean isCategoryResultsWhileVoting() {
        return Optional.ofNullable(categoryResultsWhileVoting).orElse(false);
    }

    public void setCategoryResultsWhileVoting(boolean categoryResultsWhileVoting) {
        this.categoryResultsWhileVoting = categoryResultsWhileVoting;
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

    public SchemaVersion getVersion() {
        return version;
    }

    public void setVersion(SchemaVersion version) {
        this.version = version;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isValid() {
        if (List.of(STAKE_BASED, BALANCE_BASED).contains(votingEventType)) {
            if (getStartEpoch().isEmpty() || getEndEpoch().isEmpty() || getSnapshotEpoch().isEmpty() || getVotingPowerAsset().isEmpty()) {
                return false;
            }
        }
        if (votingEventType == VotingEventType.USER_BASED) {
            if (getStartSlot().isEmpty() || getEndSlot().isEmpty()) {
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
