package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.results.VotingResultsService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/voting-results")
@Slf4j
@RequiredArgsConstructor
public class VotingResultsResource {

    private final VotingResultsService votingResultsService;

    @RequestMapping(value = "/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.results", histogram = true)
    public ResponseEntity<?> getVotingResults(@PathVariable("eventId") String eventId,
                                              @PathVariable("categoryId") String categoryId) {
        var cacheControl = CacheControl.maxAge(15, MINUTES)
                .noTransform()
                .mustRevalidate();

        return votingResultsService.getVoteResults(eventId, categoryId)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        chainTip -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(chainTip);
                        });
    }

}
