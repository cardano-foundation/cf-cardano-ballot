package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.cardano.foundation.voting.domain.OnChainEventType;

import java.util.Optional;

@Getter
@Builder
public class EventRegistrationEnvelope {

    private OnChainEventType type;
    private String name;
    private String team;
    private String schemaVersion;
    private long creationSlot;

    private boolean allowVoteChanging;

    private boolean categoryResultsWhileVoting;

    private boolean highLevelResultsWhileVoting;

    private VotingEventType votingEventType;

    @Builder.Default
    private Optional<VotingPowerAsset> votingPowerAsset = Optional.empty();

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

}
