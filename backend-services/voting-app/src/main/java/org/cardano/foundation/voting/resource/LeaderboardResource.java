package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.WinnerLeaderboardSource;
import org.cardano.foundation.voting.service.leader_board.HighLevelLeaderBoardService;
import org.cardano.foundation.voting.service.leader_board.LeaderboardWinnersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.cardano.foundation.voting.domain.WinnerLeaderboardSource.db;
import static org.cardano.foundation.voting.resource.Headers.XForceLeaderBoardResults;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.zalando.problem.Status.NOT_FOUND;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
public class LeaderboardResource {

    @Autowired
    private HighLevelLeaderBoardService highLevelLeaderBoardService;

    @Autowired
    private LeaderboardWinnersProvider leaderboardWinnersProvider;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @RequestMapping(value = "/event/{eventId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.high.level.event.available", histogram = true)
    public ResponseEntity<?> isHighLevelEventLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                                  @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = highLevelLeaderBoardService.isHighLevelEventLeaderboardAvailable(eventId, forceLeaderboard);

        return availableE.fold(problem -> {
                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .cacheControl(cacheControl)
                            .body(problem);
                },
                isAvailable -> {
                    if (isAvailable) {
                        return ResponseEntity
                                .ok()
                                .cacheControl(cacheControl)
                                .build();
                    }

                    var problem = Problem.builder()
                            .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                            .withDetail("Leaderboard not yet available for event: " + eventId)
                            .withStatus(Status.FORBIDDEN)
                            .build();


                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .cacheControl(cacheControl)
                            .body(problem);
                }
        );
    }

    @RequestMapping(value = "/event-category/{eventId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.high.level.category.available", histogram = true)
    public ResponseEntity<?> isHighLevelCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                                     @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = highLevelLeaderBoardService.isHighLevelCategoryLeaderboardAvailable(eventId, forceLeaderboard);

        return availableE.fold(problem -> {
                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .cacheControl(cacheControl)
                            .body(problem);
                },
                isAvailable -> {
                    if (isAvailable) {
                        return ResponseEntity
                                .ok()
                                .cacheControl(cacheControl)
                                .build();
                    }
                    var problem = Problem.builder()
                            .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                            .withDetail("Leaderboard not yet available for event: " + eventId)
                            .withStatus(Status.FORBIDDEN)
                            .build();

                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .cacheControl(cacheControl)
                            .body(problem);
                }
        );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.available", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(@PathVariable("eventId") String eventId,
                                                             @PathVariable("categoryId") String categoryId,
                                                             @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                             @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM
    ) {
        var winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardAvailableE = leaderboardWinnersProvider
                .getWinnerLeaderboardSource(winnerLeaderboardSource)
                .isCategoryLeaderboardAvailable(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardAvailableE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        isAvailable -> {
                            if (isAvailable) {
                                return ResponseEntity
                                        .ok()
                                        .cacheControl(cacheControl)
                                        .build();
                            }
                            var problem = Problem.builder()
                                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                                    .withDetail("Leaderboard not yet available for event: " + eventId)
                                    .withStatus(Status.FORBIDDEN)
                                    .build();

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        }
                );
    }

    @RequestMapping(value = "/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", histogram = true)
    public ResponseEntity<?> getEventLeaderBoard(@PathVariable("eventId") String eventId,
                                                 @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var eventLeaderboardE = highLevelLeaderBoardService.getEventLeaderboard(eventId, forceLeaderboard);

        return eventLeaderboardE.fold(problem -> {
                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .body(problem);
                },
                response -> {
                    return ResponseEntity
                            .ok()
                            .cacheControl(cacheControl)
                            .body(response);
                }
        );
    }

    @Deprecated
    @RequestMapping(value = "/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardPerCategory(@PathVariable("eventId") String eventId,
                                                    @PathVariable("categoryId") String categoryId,
                                                    @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                    @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        var winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderboardWinnersProvider
                .getWinnerLeaderboardSource(winnerLeaderboardSource)
                .getCategoryLeaderboard(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        proposalsInCategoryStatsM -> {
                            if (proposalsInCategoryStatsM.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("VOTING_RESULTS_NOT_YET_AVAILABLE")
                                        .withDetail("Leaderboard not yet available for event: " + eventId)
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .body(problem);
                            }

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(proposalsInCategoryStatsM.orElseThrow());
                        }
                );
    }

    @RequestMapping(value = "/{eventId}/{categoryId}/results", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardPerCategoryResults(@PathVariable("eventId") String eventId,
                                                               @PathVariable("categoryId") String categoryId,
                                                               @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                               @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        var winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderboardWinnersProvider
                .getWinnerLeaderboardSource(winnerLeaderboardSource)
                .getCategoryLeaderboard(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        proposalsInCategoryStatsM -> {
                            if (proposalsInCategoryStatsM.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("VOTING_RESULTS_NOT_YET_AVAILABLE")
                                        .withDetail("Leaderboard not yet available for event: " + eventId)
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .body(problem);
                            }

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(proposalsInCategoryStatsM.orElseThrow());
                        }
                );
    }

    @RequestMapping(value = "/{eventId}/results", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.all", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardForAllCategoriesResults(@PathVariable("eventId") String eventId,
                                                                    @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                                    @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        var winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        var cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderboardWinnersProvider
                .getWinnerLeaderboardSource(winnerLeaderboardSource)
                .getAllCategoriesLeaderboard(eventId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        allCategoryResults -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(allCategoryResults);
                        }
                );
    }

}
