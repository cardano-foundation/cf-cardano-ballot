package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TallyResults;
import org.cardano.foundation.voting.service.results.VotingTallyService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.problem.Status.NOT_FOUND;

@RestController
@RequestMapping("/api/tally/voting-results")
@Slf4j
@RequiredArgsConstructor
public class VotingTallyResource {

    private final VotingTallyService votingTallyService;

    @RequestMapping(value = "/{eventId}/{categoryId}/{tallyName}", method = GET, produces = "application/json")
    @Timed(value = "resource.tally.results", histogram = true)
    @Operation(summary = "Get L1 tally results by a given eventId, categoryId and tallyName",
            description = "Gets tally results.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deliver L1 tally results",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TallyResults.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tally results not yet available"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getVoteResults(@Parameter(description = "Event ID for which details are to be retrieved", required = true)
                                            @PathVariable("eventId") String eventId,
                                            @Parameter(description = "Category ID for which details are to be retrieved", required = true)
                                            @PathVariable("categoryId") String categoryId,
                                            @Parameter(description = "tallyName as registered on chain", required = true)
                                            @PathVariable("tallyName") String tallyName) {
        var cacheControl = CacheControl.maxAge(15, MINUTES)
                .noTransform()
                .mustRevalidate();

        return votingTallyService.getVoteResults(eventId, categoryId, tallyName)
                .fold(problem -> {
                        return ResponseEntity
                                .status(problem.getStatus().getStatusCode())
                                .body(problem);
                        },
                        tallyResultsM -> {
                            if (tallyResultsM.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("NO_RESULTS_FOUND")
                                        .withDetail("No results found for event: " + eventId + " and category: " + categoryId)
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .body(problem);
                            }

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(tallyResultsM.orElseThrow());
                        });
    }

}
