package org.cardano.foundation.voting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.entity.Tally;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
@Getter
@Schema(description = "Voting Results")
public class TallyResults {

    @Schema(description = "Name of the tally (voting results)", requiredMode = REQUIRED)
    private String tallyName;

    @Schema(description = "Optional human readable description of the tally (voting results)", requiredMode = NOT_REQUIRED)
    private Optional<String> tallyDescription;

    @Schema(description = "Optional human readable description of the tally (voting results)", requiredMode = REQUIRED)
    private Tally.TallyType tallyType;

    @Schema(description = "Binding of eventId and tally", requiredMode = REQUIRED)
    private String eventId;

    @Schema(description = "Binding of categoryId and tally", requiredMode = REQUIRED)
    private String categoryId;

    @Schema(description = "Actual voting results, it's a map of proposalId to vote score.", requiredMode = REQUIRED)
    private Map<String, Long> results;

    @Builder.Default
    @Schema(description = "Additional tally metadata which is specific for a certain tally type.", requiredMode = NOT_REQUIRED)
    private Map<String, Object> metadata = new HashMap<>();

}
