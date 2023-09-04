package org.cardano.foundation.voting.domain.presentation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@Builder
public class ProposalPresentation {

    private String id;

    @Builder.Default
    private Optional<String> name = Optional.empty();

}
