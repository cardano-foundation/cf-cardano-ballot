package org.cardano.foundation.voting.domain.presentation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cardano.foundation.voting.domain.VotingEventType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
public class EventPresentation {

    private String id;

    private String team; // e.g. CF Team

    private VotingEventType votingEventType;

    @Builder.Default
    private Optional<Long> startSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> endSlot = Optional.empty();

    @Builder.Default
    private Optional<Integer> startEpoch = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> eventStart = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> eventEnd = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> snapshotTime = Optional.empty();

    @Builder.Default
    private Optional<Integer> endEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> snapshotEpoch = Optional.empty();

    @Builder.Default
    private boolean isActive = false;

    @Builder.Default
    private boolean isFinished = false;

    @Builder.Default
    private boolean isNotStarted = false;

    @Builder.Default
    private boolean isAllowVoteChanging = false;

    @Builder.Default
    private boolean isHighLevelResultsWhileVoting = false;

    @Builder.Default
    private boolean isCategoryResultsWhileVoting = false;

    @Builder.Default
    private List<CategoryPresentation> categories = new ArrayList<>();

}
