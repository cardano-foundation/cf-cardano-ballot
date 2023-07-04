package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;

import javax.annotation.Nullable;

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

    private VotingEventType votingEventType;

    @Nullable private Integer startEpoch;

    @Nullable private Integer endEpoch;

    @Nullable private Long startSlot;

    @Nullable private Long endSlot;

    @Nullable private Integer snapshotEpoch;

}
