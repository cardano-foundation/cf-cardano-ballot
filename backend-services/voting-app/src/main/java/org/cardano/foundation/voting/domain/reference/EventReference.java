package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cardano.foundation.voting.domain.VotingEventType;

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

    private boolean gdprProtection;

    private Optional<Long> startSlot;

    private Optional<Long> endSlot;

    private Optional<Integer> startEpoch;

    private Optional<Integer> endEpoch;

    private Optional<Integer> snapshotEpoch;

    private List<CategoryReference> categories;

}
