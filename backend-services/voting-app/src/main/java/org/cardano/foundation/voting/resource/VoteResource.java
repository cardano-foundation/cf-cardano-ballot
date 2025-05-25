package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.UserVotes;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.service.auth.jwt.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.vote.DefaultVoteService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_ACCEPTABLE;

@RestController
@RequestMapping("/api/vote")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Vote", description = "Operations related to voting")
public class VoteResource {

    private final DefaultVoteService voteService;

    @RequestMapping(value = "/votes/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.votes", histogram = true)
    @Operation(
            summary = "Retrieve votes for an event",
            description = "Fetch all votes associated with a given event. If an eventId is provided in the path, it should match the one in the JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of votes for the event.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            array = @ArraySchema( schema= @Schema(implementation = UserVotes.class))) // Assuming the response is of type CategoryProposalPairs
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, possibly due to JWT missing or event ID mismatch.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class)) // Using the Problem class you provided earlier
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getVotes(
            @Parameter(name = "eventId", required = false, description = "ID of the event for which votes are being fetched.")
            @PathVariable(value = "eventId", required = false) Optional<String> eventIdM,
            Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth Bearer Auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        if (eventIdM.isPresent() && !eventIdM.orElseThrow().equals(jwtAuth.eventDetails().id())) {
            var problem = Problem.builder()
                    .withTitle("EVENT_ID_MISMATCH")
                    .withDetail("Event id in path and in JWT token do not match!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.getVotes(jwtAuth)
                .fold(problem -> {
                            log.warn("Vote get voted on failed, problem:{}", problem);

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        categoryProposalPairs -> {
                            var cacheControl = CacheControl.maxAge(1, MINUTES)
                                    .noTransform()
                                    .mustRevalidate();

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(categoryProposalPairs);
                        });
    }

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.cast", histogram = true)
    @Operation(summary = "Cast a vote", description = "Allows users to cast their vote. Requires authentication.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Vote successfully cast."),
                @ApiResponse(responseCode = "400", description = "Bad request, possibly due to missing Web3 authentication.",
                        content = {
                                @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Problem.class))
                        }
                ),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> castVote(Authentication authentication) {
        log.info("Casting vote...");

        if (!(authentication instanceof Web3AuthenticationToken web3Auth)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("Auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.castVote(web3Auth)
                .fold(problem -> {
                            log.warn("Vote cast failed, problem:{}", problem);

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        vote -> {
                            log.info("Vote cast just fine.");

                            return ResponseEntity
                                    .ok()
                                    .build();
                        });
    }

    @RequestMapping(value = "/receipt", method = { HEAD, GET } , produces = "application/json")
    @Timed(value = "resource.vote.receipt.web3", histogram = true)
    @Operation(summary = "Retrieve a vote receipt", description = "Allows users to retrieve a receipt for their vote. Requires authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vote receipt retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VoteReceipt.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, possibly due to missing Web3 authentication.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getVoteReceipt(Authentication authentication) {
        if (!(authentication instanceof Web3AuthenticationToken web3Auth)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("Auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.voteReceipt(web3Auth)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        voteReceipt -> {
                            var cacheControl = CacheControl.maxAge(1, MINUTES)
                                    .noTransform()
                                    .mustRevalidate();

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/receipts", method = { GET }, produces = "application/json")
    @Timed(value = "resource.vote.receipts", histogram = true)
    @Operation(
            summary = "Retrieve all vote receipts for the authenticated user",
            description = "Allows users to retrieve all vote receipts for their votes. Requires JWT authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vote receipts retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json", schema = @Schema(implementation = VoteReceipt.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to missing JWT authentication.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getAllVoteReceipts(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.voteReceipts(jwtAuth)
                .fold(problem -> {
                            log.warn("Failed to retrieve vote receipts, problem: {}", problem);

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        voteReceipts -> {
                            var cacheControl = CacheControl.maxAge(1, MINUTES)
                                    .noTransform()
                                    .mustRevalidate();

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(voteReceipts);
                        });
    }

    @RequestMapping(value = "/receipt/{eventId}/{categoryId}", method = { HEAD, GET }, produces = "application/json")
    @Timed(value = "resource.vote.receipt.jwt", histogram = true)
    @Operation(
            summary = "Retrieve a vote receipt for a specific category and event",
            description = "Allows users to retrieve a receipt for their vote for a specified category within an event. Requires JWT authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vote receipt retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json", schema = @Schema(implementation = VoteReceipt.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to missing JWT authentication or mismatched event ID.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getVoteReceipt(@PathVariable(value = "eventId", required = false) Optional<String> maybeEventId,
                                            @PathVariable("categoryId") String categoryId,
                                            Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        if (maybeEventId.isPresent() && !maybeEventId.orElseThrow().equals(jwtAuth.eventDetails().id())) {
            var problem = Problem.builder()
                    .withTitle("EVENT_ID_MISMATCH")
                    .withDetail("Event id in path and in JWT token do not match!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.voteReceipt(categoryId, jwtAuth)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        voteReceipt -> {
                            var cacheControl = CacheControl.maxAge(1, MINUTES)
                                    .noTransform()
                                    .mustRevalidate();

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/vote-changing-available/{eventId}/{voteId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.vote.vote.changing.available", histogram = true)
    @Operation(summary = "Check if vote changing is available for a specific event and vote",
            description = "Determines if a user's vote can be changed for a specific vote within an event. Requires JWT authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vote changing is available."),
                    @ApiResponse(responseCode = "406", description = "Vote changing is not available.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to missing JWT authentication or mismatched event ID.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )

    public ResponseEntity<?> isVoteChangingAvailable(@PathVariable(value = "eventId", required = false) Optional<String> maybeEventId,
                                                     @PathVariable("voteId") String voteId,
                                                     Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth Bearer Auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        if (maybeEventId.isPresent() && maybeEventId.orElseThrow().equals(jwtAuth.eventDetails().id())) {
            var problem = Problem.builder()
                    .withTitle("EVENT_ID_MISMATCH")
                    .withDetail("Event id in path and in JWT token do not match!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return voteService.isVoteChangingPossible(voteId, jwtAuth)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        isAvailable -> {
                            if (isAvailable) {
                                return ResponseEntity
                                        .ok()
                                        .build();
                            }
                            var problem = Problem.builder()
                                    .withTitle("VOTE_CHANGING_NOT_AVAILABLE")
                                    .withDetail("Vote changing is not available for this vote!")
                                    .withStatus(NOT_ACCEPTABLE)
                                    .build();

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        });
    }

}
