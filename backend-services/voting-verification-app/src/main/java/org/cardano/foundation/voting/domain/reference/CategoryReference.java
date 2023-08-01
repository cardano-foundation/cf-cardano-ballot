package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class CategoryReference {

    private String id;

    private boolean gdprProtection;

    private String presentationName;

    @Builder.Default
    private List<ProposalReference> proposals = new ArrayList<>();


}
