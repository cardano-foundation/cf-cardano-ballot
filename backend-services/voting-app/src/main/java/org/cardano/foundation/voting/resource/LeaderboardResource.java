package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.leader_board.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
public class LeaderboardResource {

    @Autowired
    private LeaderBoardService leaderBoardService;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @RequestMapping(value = "/{eventId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.event.available", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> isEventLeaderBoardAvailable(@PathVariable("eventId") String eventId) {
        return leaderBoardService.isEventLeaderboardAvailable(eventId, false)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build()
                );
    }

    @RequestMapping(value = "/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getEventLeaderBoard(@PathVariable("eventId") String eventId,
                                                 @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        return leaderBoardService.getEventLeaderboard(eventId, forceLeaderboard)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getCategoryLeaderBoard(@PathVariable("eventId") String eventId,
                                                    @PathVariable("categoryId") String categoryId,
                                                    @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        return leaderBoardService.getCategoryLeaderboard(eventId, categoryId, forceLeaderboard)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.available", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                             @PathVariable("categoryId") String categoryId) {
        return leaderBoardService.isCategoryLeaderboardAvailable(eventId, categoryId, false)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build()
                );
    }

}
