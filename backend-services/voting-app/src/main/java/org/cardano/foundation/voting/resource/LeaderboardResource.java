package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.WinnerLeaderboardSource;
import org.cardano.foundation.voting.service.leader_board.HighLevelLeaderBoardService;
import org.cardano.foundation.voting.service.leader_board.LeaderboardWinnersProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.cardano.foundation.voting.domain.WinnerLeaderboardSource.db;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_Force_LeaderBoard_Results;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.zalando.problem.Status.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
@Tag(name = "Leaderboard", description = "Operations related to the leaderboard")
public class LeaderboardResource {

    private final HighLevelLeaderBoardService highLevelLeaderBoardService;

    private final LeaderboardWinnersProvider leaderboardWinnersProvider;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @RequestMapping(value = "/event/{eventId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.leaderboard.high.level.event.available", histogram = true)
    @Operation(summary = "Check availability of the high-level event leaderboard",
            description = "Verifies if the high-level leaderboard for a specific event is available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leaderboard is available"),
                    @ApiResponse(responseCode = "403", description = "Leaderboard not available yet for event",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> isHighLevelEventLeaderBoardAvailable(@Parameter(name = "eventId", description = "ID of the event", required = true)
                                                                  @PathVariable("eventId") String eventId,
                                                                  @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val availableE = highLevelLeaderBoardService.isHighLevelEventLeaderboardAvailable(eventId, forceLeaderboard);

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

                    val problem = Problem.builder()
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
    @Operation(summary = "Check availability of the high-level category leaderboard for an event",
            description = "Verifies if the high-level category leaderboard for a specific event is available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category leaderboard is available"),
                    @ApiResponse(responseCode = "403", description = "Category leaderboard not available",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> isHighLevelCategoryLeaderBoardAvailable(@Parameter(name = "eventId", description = "ID of the event", required = true)
                                                                     @PathVariable("eventId") String eventId,
                                                                     @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val availableE = highLevelLeaderBoardService.isHighLevelCategoryLeaderboardAvailable(eventId, forceLeaderboard);

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
                    val problem = Problem.builder()
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
    @Operation(summary = "Check if Category Leaderboard is Available",
            description = "Checks if the leaderboard for the specified event and category is available.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category leaderboard is available"),
                    @ApiResponse(responseCode = "403", description = "Category leaderboard not available",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(@Parameter(name = "eventId", description = "ID of the event", required = true)
                                                             @PathVariable("eventId") String eventId,
                                                             @Parameter(name = "categoryId", description = "ID of the category", required = true)
                                                             @PathVariable("categoryId") String categoryId,
                                                             @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                             @Parameter(name = "source", description = "source of results, db or l1")
                                                             @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        val winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val categoryLeaderboardAvailableE = leaderboardWinnersProvider
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
                            val problem = Problem.builder()
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
    @Operation(
            summary = "Retrieve Event Leaderboard",
            description = "Fetches the leaderboard for the specified event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved leaderboard for the specified event."),
                    @ApiResponse(responseCode = "400", description = "Bad request or incorrect event ID."),
                    @ApiResponse(responseCode = "403", description = "Leaderboard not yet available for the specified event."),
                    @ApiResponse(responseCode = "500", description = "Internal server error or other issues.")
            }
    )
    public ResponseEntity<?> getEventLeaderBoard(
                                                 @Parameter(name = "eventId", description = "ID of the event", required = true)
                                                 @PathVariable("eventId") String eventId,
                                                 @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val eventLeaderboardE = highLevelLeaderBoardService.getEventLeaderboard(eventId, forceLeaderboard);

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
    @Operation(
            summary = "Retrieve Category Leaderboard for an Event",
            description = "Fetches the leaderboard for a specified category within an event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved leaderboard for the specified category in the event."),
                    @ApiResponse(responseCode = "400", description = "Bad request, incorrect event ID or category ID."),
                    @ApiResponse(responseCode = "403", description = "Leaderboard not yet available for the specified category in the event."),
                    @ApiResponse(responseCode = "500", description = "Internal server error or other issues.")
            }
    )
    public ResponseEntity<?> getCategoryLeaderBoard(@Parameter(name = "eventId", description = "ID of the event", required = true)
                                                    @PathVariable("eventId") String eventId,
                                                    @Parameter(name = "categoryId", description = "ID of the category", required = true)
                                                    @PathVariable("categoryId") String categoryId,
                                                    @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                    @Parameter(name = "source", description = "source of results, db or l1")
                                                    @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        val winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val categoryLeaderboardE = leaderboardWinnersProvider
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
                                val problem = Problem.builder()
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
                                                               @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                               @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        val winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val categoryLeaderboardE = leaderboardWinnersProvider
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
                                val problem = Problem.builder()
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
                                                                    @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                                    @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        val winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val categoryLeaderboardE = leaderboardWinnersProvider
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
