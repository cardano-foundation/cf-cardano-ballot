package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.auth.jwt.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.vote.VoteService;
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
public class VoteResource {

    private final VoteService voteService;

    @RequestMapping(value = "/votes/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.votes", histogram = true)
    public ResponseEntity<?> getVotes(@PathVariable(value = "eventId", required = false) Optional<String> maybeEventId,
                                      Authentication authentication) {
        var cacheControl = CacheControl.noCache()
                .noTransform()
                .mustRevalidate();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth Bearer Auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .cacheControl(cacheControl)
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
                    .cacheControl(cacheControl)
                    .body(problem);
        }

        return voteService.getVotes(jwtAuth)
                .fold(problem -> {
                            log.warn("Vote get voted on failed, problem:{}", problem);

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        categoryProposalPairs -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(categoryProposalPairs);
                        });
    }

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.cast", histogram = true)
    public ResponseEntity<?> castVote(Authentication authentication) {
        log.info("Casting vote...");

        if (!(authentication instanceof Web3AuthenticationToken web3Auth)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
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

    @RequestMapping(value = "/receipt", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt.web3", histogram = true)
    public ResponseEntity<?> getVoteReceipt(Authentication authentication) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        if (!(authentication instanceof Web3AuthenticationToken web3Auth)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .cacheControl(cacheControl)
                    .body(problem);
        }

        return voteService.voteReceipt(web3Auth)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        voteReceipt -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/receipt/{maybeEventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt.jwt", histogram = true)
    public ResponseEntity<?> getVoteReceipt(@PathVariable(value = "eventId", required = false) Optional<String> maybeEventId,
                                            @PathVariable("categoryId") String categoryId,
                                            Authentication authentication) {
        var cacheControl = CacheControl.maxAge(1, MINUTES)
                .noTransform()
                .mustRevalidate();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .cacheControl(cacheControl)
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
                    .cacheControl(cacheControl)
                    .body(problem);
        }

        return voteService.voteReceipt(categoryId, jwtAuth)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        voteReceipt -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/vote-changing-available/{eventId}/{voteId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.voteId.receipt", histogram = true)
    public ResponseEntity<?> isVoteChangingAvailable(@PathVariable(value = "eventId", required = false) Optional<String> maybeEventId,
                                                     @PathVariable("voteId") String voteId,
                                                     Authentication authentication) {
        var cacheControl = CacheControl.noCache()
                .noTransform()
                .mustRevalidate();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth Bearer Auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .cacheControl(cacheControl)
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
                    .cacheControl(cacheControl)
                    .body(problem);
        }

        return voteService.isVoteChangingPossible(voteId, jwtAuth)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .cacheControl(cacheControl)
                        .body(problem),
                        isAvailable -> {
                            if (isAvailable) {
                                return ResponseEntity
                                        .ok()
                                        .cacheControl(cacheControl)
                                        .build();
                            }
                            var problem = Problem.builder()
                                    .withTitle("VOTE_CHANGING_NOT_AVAILABLE")
                                    .withDetail("Vote changing is not available for this vote!")
                                    .withStatus(NOT_ACCEPTABLE)
                                    .build();

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        });
    }

}
