package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
@Builder
public class EventReference {

    private String id; // e.g. 90ed2df9-dd21-4567-90e2-e8f09b9c422c

    private String team; // e.g. CF Team

    private String name; // e.g. Voltaire_Pre_Ratification

    private String presentationName; // e.g. Voltaire Pre-Ratification

    @Nullable
    private String description;

    private int startSlot;

    private int endSlot;

    private int snapshotEpoch;

    private List<CategoryReference> categories;

}
