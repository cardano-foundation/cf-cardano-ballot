package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProposalPresentation {

    private String id;

    private String name;

    private String presentationName;

}
