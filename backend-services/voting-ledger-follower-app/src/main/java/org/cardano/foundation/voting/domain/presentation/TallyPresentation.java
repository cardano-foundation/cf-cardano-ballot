package org.cardano.foundation.voting.domain.presentation;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.entity.Tally;

import java.util.Optional;

@Builder
@Getter
public class TallyPresentation {

    private String name;
    @Builder.Default
    private Optional<String> description = Optional.empty();
    private Tally.TallyType type;

    @Builder.Default
    private Optional<HydraTallyConfigPresentation> config = Optional.empty();

}
