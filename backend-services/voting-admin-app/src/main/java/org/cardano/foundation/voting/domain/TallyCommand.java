package org.cardano.foundation.voting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TallyCommand {

    private final String name;

    private final String description;

    private final TallyType type;

    private final TallyMode mode;

    private final int independentPartiesCount;

    private final Object config;

}
