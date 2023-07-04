package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;

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

    private VotingEventType votingEventType;

    @Nullable
    private Integer startEpoch;

    @Nullable
    private Integer endEpoch;

    @Nullable
    private Long startSlot;

    @Nullable
    private Long endSlot;

    @Nullable
    private Integer snapshotEpoch;

    @Builder.Default
    private SchemaVersion version = SchemaVersion.V1;

}
