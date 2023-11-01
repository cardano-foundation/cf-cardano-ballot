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

    private String organisers; // e.g. CF

    private VotingEventType votingEventType;

    @Builder.Default
    private Optional<Long> startSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> endSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> proposalsRevealSlot = Optional.empty();

    @Builder.Default
    private Optional<Integer> startEpoch = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> eventStartDate = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> eventEndDate = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> proposalsRevealDate = Optional.empty();

    @Builder.Default
    private Optional<ZonedDateTime> snapshotTime = Optional.empty();

    @Builder.Default
    private Optional<Integer> endEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> snapshotEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> proposalsRevealEpoch = Optional.empty();

    @Builder.Default
    private boolean isActive = false;

    @Builder.Default
    private boolean isFinished = false;

    @Builder.Default
    private boolean isNotStarted = false;

    @Builder.Default
    private boolean isStarted = false;

    @Builder.Default
    private boolean isProposalsReveal = false;

    @Builder.Default
    private boolean isCommitmentsWindowOpen = false;

    @Builder.Default
    private boolean isAllowVoteChanging = false;

    @Builder.Default
    private boolean isHighLevelEventResultsWhileVoting = false;

    @Builder.Default
    private boolean isHighLevelCategoryResultsWhileVoting = false;

    @Builder.Default
    private boolean isCategoryResultsWhileVoting = false;

    @Builder.Default
    private List<CategoryPresentation> categories = new ArrayList<>();

    @Builder.Default
    private List<TallyPresentation> tallies = new ArrayList<>();

}
