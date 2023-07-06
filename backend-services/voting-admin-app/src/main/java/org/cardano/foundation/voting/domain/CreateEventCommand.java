package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;

@Getter
@Builder
@ToString
public class CreateEventCommand {

    private String id; // e.g. Voltaire_Pre_Ratification

    private String team; // e.g. CF Team // TODO what about team spoofing - do we need that team has private / public key

    @Builder.Default
    private boolean allowVoteChanging = false; // until merkle root is committed to the chain, do we allow vote changing?

    @Builder.Default
    private boolean categoryResultsWhileVoting = false; // until voting is finished, do we actually allow people to see results within category?

    @Builder.Default
    private VotingEventType votingEventType = VotingEventType.STAKE_BASED;

    @Builder.Default
    private Optional<VotingPowerAsset> votingPowerAsset = Optional.of(ADA); // this field makes sense only for stake based voting

    @Builder.Default
    private Optional<Integer> startEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> endEpoch = Optional.empty();

    @Builder.Default
    private Optional<Long> startSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> endSlot = Optional.empty();

    @Builder.Default
    private Optional<Integer> snapshotEpoch = Optional.empty();

    @Builder.Default
    private SchemaVersion version = SchemaVersion.V1;

}
