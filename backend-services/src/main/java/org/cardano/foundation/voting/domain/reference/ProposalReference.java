package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProposalReference {

    private String id;

    private String name;

    private String presentationName;

}
