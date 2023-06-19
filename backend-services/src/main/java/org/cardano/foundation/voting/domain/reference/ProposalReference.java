package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
@Builder
public class ProposalReference {

    private String id;

    private String name;

    private String presentationName;

    @Nullable
    private String description;

}
