package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;

@Getter
@Builder
@ToString
public class CreateEventCommand {

    private String id;

    private String organisers;

    @Builder.Default
    private boolean allowVoteChanging = false; // until merkle root is committed to the chain, we could allow vote changing

    @Builder.Default
    private boolean highLevelEventResultsWhileVoting = false;

    @Builder.Default
    private boolean highLevelCategoryResultsWhileVoting = false;

    @Builder.Default
    private boolean categoryResultsWhileVoting = false;

    @Builder.Default
    private VotingEventType votingEventType = VotingEventType.STAKE_BASED;

    @Builder.Default
    private Optional<VotingPowerAsset> votingPowerAsset = Optional.of(ADA); // this field makes sense only for stake based voting

    @Builder.Default
    private Optional<Integer> startEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> endEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> snapshotEpoch = Optional.empty();

    @Builder.Default
    private Optional<Long> startSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> endSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> proposalsRevealSlot = Optional.empty();

    @Builder.Default
    private Optional<Integer> proposalsRevealEpoch = Optional.empty();

    @Builder.Default
    private List<TallyCommand> tallies = List.of();

    @Builder.Default
    private SchemaVersion schemaVersion = SchemaVersion.V11;

}
