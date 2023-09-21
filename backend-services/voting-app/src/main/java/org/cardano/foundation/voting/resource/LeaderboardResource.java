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

import static org.cardano.foundation.voting.resource.Headers.XForceLeaderBoardResults;
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

    @RequestMapping(value = "/event/{eventId}/", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.high.level.event.available", histogram = true)
    public ResponseEntity<?> isHighLevelEventLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                                  @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = leaderBoardService.isHighLevelEventLeaderboardAvailable(eventId, forceLeaderboard);

        return availableE.fold(problem -> {
                    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                },
                isAvailable -> {
                    return isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build();
                }
        );
    }

    @RequestMapping(value = "/event-category/{eventId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.high.level.category.available", histogram = true)
    public ResponseEntity<?> isHighLevelCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                                     @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = leaderBoardService.isHighLevelCategoryLeaderboardAvailable(eventId, forceLeaderboard);

        return availableE.fold(problem -> {
                    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                },
                isAvailable -> {
                    return isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build();
                }
        );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.available", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                             @PathVariable("categoryId") String categoryId,
                                                             @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardAvailableE = leaderBoardService.isCategoryLeaderboardAvailable(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardAvailableE
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        isAvailable -> {
                            return isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(FORBIDDEN).build();
                        }
                );
    }

    @RequestMapping(value = "/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", histogram = true)
    public ResponseEntity<?> getEventLeaderBoard(@PathVariable("eventId") String eventId,
                                                 @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var eventLeaderboardE = leaderBoardService.getEventLeaderboard(eventId, forceLeaderboard);

        return eventLeaderboardE.fold(problem -> {
                    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                },
                response -> {
                    return ResponseEntity.ok().body(response);
                }
        );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoard(@PathVariable("eventId") String eventId,
                                                    @PathVariable("categoryId") String categoryId,
                                                    @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderBoardService.getCategoryLeaderboard(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        response -> {
                            return ResponseEntity.ok().body(response);
                        }
                );
    }

    @RequestMapping(value = "/{eventId}/winners", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.winners", histogram = true)
    public ResponseEntity<?> getWinners(@PathVariable("eventId") String eventId,
                                        @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderBoardService.getEventWinners(eventId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        response -> {
                            return ResponseEntity.ok().body(response);
                        }
                );
    }

}
