package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.service.leader_board.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

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
    @Timed(value = "resource.leaderboard.event.available", histogram = true)
    public ResponseEntity<?> isEventLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                         @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        Either<Problem, Boolean> eventLeaderboardAvailableE = leaderBoardService.isEventLeaderboardAvailable(eventId, forceLeaderboard);

        return eventLeaderboardAvailableE
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build()
                );
    }

    @RequestMapping(value = "/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", histogram = true)
    public ResponseEntity<?> getEventLeaderBoard(@PathVariable("eventId") String eventId,
                                                 @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        Either<Problem, Leaderboard.ByEvent> eventLeaderboardE = leaderBoardService.getEventLeaderboard(eventId, forceLeaderboard);

        return eventLeaderboardE
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoard(@PathVariable("eventId") String eventId,
                                                    @PathVariable("categoryId") String categoryId,
                                                    @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        Either<Problem, Leaderboard.ByCategory> categoryLeaderboardE = leaderBoardService.getCategoryLeaderboard(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.available", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                             @PathVariable("categoryId") String categoryId,
                                                             @RequestHeader(value = "force-leaderboard-results", required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        Either<Problem, Boolean> categoryLeaderboardAvailableE = leaderBoardService.isCategoryLeaderboardAvailable(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardAvailableE
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build()
                );
    }

}
