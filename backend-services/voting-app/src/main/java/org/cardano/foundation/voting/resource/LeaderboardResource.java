package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.leader_board.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.cardano.foundation.voting.resource.Headers.XForceLeaderBoardResults;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
@Tag(name = "Leaderboard", description = "Operations related to the leaderboard")
public class LeaderboardResource {

    @Autowired
    private LeaderBoardService leaderBoardService;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @RequestMapping(value = "/event/{eventId}/", method = HEAD, produces = "application/json")
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
    public ResponseEntity<?> isHighLevelEventLeaderBoardAvailable(
            @Parameter(name = "eventId", description = "ID of the event", required = true)
            @PathVariable("eventId") String eventId,
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = leaderBoardService.isHighLevelEventLeaderboardAvailable(eventId, forceLeaderboard);

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
    public ResponseEntity<?> isHighLevelCategoryLeaderBoardAvailable(
            @Parameter(name = "eventId", description = "ID of the event", required = true)
            @PathVariable("eventId") String eventId,
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var availableE = leaderBoardService.isHighLevelCategoryLeaderboardAvailable(eventId, forceLeaderboard);

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
    public ResponseEntity<?> getCategoryLeaderBoardAvailable(
            @Parameter(name = "eventId", description = "ID of the event", required = true)
            @PathVariable("eventId") String eventId,
            @Parameter(name = "categoryId", description = "ID of the category", required = true)
            @PathVariable("categoryId") String categoryId,
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardAvailableE = leaderBoardService.isCategoryLeaderboardAvailable(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardAvailableE
                .fold(problem -> {
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
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var eventLeaderboardE = leaderBoardService.getEventLeaderboard(eventId, forceLeaderboard);

        return eventLeaderboardE.fold(problem -> {
                    return ResponseEntity
                            .status(problem.getStatus().getStatusCode())
                            .cacheControl(cacheControl)
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
    public ResponseEntity<?> getCategoryLeaderBoard(
            @Parameter(name = "eventId", description = "ID of the event", required = true)
            @PathVariable("eventId") String eventId,
            @Parameter(name = "categoryId", description = "ID of the category", required = true)
            @PathVariable("categoryId") String categoryId,
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderBoardService.getCategoryLeaderboard(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
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

    @RequestMapping(value = "/{eventId}/winners", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category.winners", histogram = true)
    @Operation(
            summary = "Retrieve Winners for an Event",
            description = "Fetches the winners for a specified event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved winners for the event."),
                    @ApiResponse(responseCode = "400", description = "Bad request, incorrect event ID."),
                    @ApiResponse(responseCode = "403", description = "Winners not yet available for the event."),
                    @ApiResponse(responseCode = "500", description = "Internal server error or other issues.")
            }
    )
    public ResponseEntity<?> getWinners(
            @Parameter(name = "eventId", description = "ID of the event", required = true)
            @PathVariable("eventId") String eventId,
            @RequestHeader(value = XForceLeaderBoardResults, required = false, defaultValue = "false") boolean forceLeaderboardResults) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        var forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        var categoryLeaderboardE = leaderBoardService.getEventWinners(eventId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
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

}
