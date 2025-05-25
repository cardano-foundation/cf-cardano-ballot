package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.vote.CandidateVoteService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.zalando.problem.Status.BAD_REQUEST;

@RestController
@RequestMapping("/api/vote/candidate")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Vote Candidate", description = "Operations related to voting for candidates")
public class VoteCandidateResource {

    private final CandidateVoteService voteService;

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.candidate.cast", histogram = true)
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
    @Timed(value = "resource.vote.candidate.receipt.web3", histogram = true)
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

}
