package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CategoryReference {

    private String id;

    private boolean gdprProtection;

    private String presentationName;

    private List<ProposalReference> proposals;


}
