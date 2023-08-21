package org.cardano.foundation.voting.domain.presentation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class CategoryPresentation {

    private String id;

    private boolean gdprProtection;

    private String presentationName;

    @Builder.Default
    private List<ProposalPresentation> proposals = new ArrayList<>();


}
