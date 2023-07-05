package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cardano.foundation.voting.domain.VotingEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
public class EventReference {

    private String id; // e.g. Voltaire_Pre_Ratification

    private String team; // e.g. CF Team

    private String presentationName; // e.g. Voltaire Pre-Ratification

    private VotingEventType votingEventType;

    @Builder.Default
    private Optional<Long> startSlot = Optional.empty();

    @Builder.Default
    private Optional<Long> endSlot = Optional.empty();

    @Builder.Default
    private Optional<Integer> startEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> endEpoch = Optional.empty();

    @Builder.Default
    private Optional<Integer> snapshotEpoch = Optional.empty();

    @Builder.Default
    private boolean isActive = false;

    @Builder.Default
    private List<CategoryReference> categories = new ArrayList<>();

}
