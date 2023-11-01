package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.entity.Tally;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
@Getter
public class TallyResults {

    private String tallyName;
    private Optional<String> tallyDescription;
    private Tally.TallyType tallyType;
    private String eventId;
    private String categoryId;

    private Map<String, Long> results;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

}
