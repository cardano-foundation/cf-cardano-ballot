package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Timed(value = "resource.tally.results.per.category", histogram = true)
    public ResponseEntity<?> getVoteResultsPerCategory(@PathVariable("eventId") String eventId,
                                            @PathVariable("categoryId") String categoryId,
                                            @PathVariable("tallyName") String tallyName) {
        var cacheControl = CacheControl.maxAge(15, MINUTES)
                .noTransform()
                .mustRevalidate();

        return votingTallyService.getVoteResultsPerCategory(eventId, categoryId, tallyName)
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

    @RequestMapping(value = "/{eventId}/{tallyName}", method = GET, produces = "application/json")
    @Timed(value = "resource.tally.results.per.all.categories", histogram = true)
    public ResponseEntity<?> getVoteResultsForAllCategories(@PathVariable("eventId") String eventId,
                                            @PathVariable("tallyName") String tallyName) {
        var cacheControl = CacheControl.maxAge(15, MINUTES)
                .noTransform()
                .mustRevalidate();

        return votingTallyService.getVoteResultsForAllCategories(eventId, tallyName)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        tallyResults -> {
                            if (tallyResults.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("NO_RESULTS_FOUND")
                                        .withDetail("No results found for event: " + eventId)
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .body(problem);
                            }

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(tallyResults);
                        });
    }

}
