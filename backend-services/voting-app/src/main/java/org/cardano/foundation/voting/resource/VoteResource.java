package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.auth.jwt.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.zalando.problem.Status.BAD_REQUEST;

@RestController
@RequestMapping("/api/vote")
@Slf4j
@RequiredArgsConstructor
public class VoteResource {

    private final VoteService voteService;

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.cast", histogram = true)
    public ResponseEntity<?> castVote(Authentication authentication) {
        log.info("Casting vote...");

        if (!(authentication instanceof Web3AuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
        }

        return voteService.castVote((Web3AuthenticationToken) authentication)
                .fold(problem -> {
                            log.warn("Vote cast failed, problem:{}", problem);

                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        vote -> {
                            log.info("Vote cast just fine.");

                            return ResponseEntity.ok().build();
                        });
    }

    @RequestMapping(value = "/receipt", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt.web3", histogram = true)
    public ResponseEntity<?> getVoteReceipt(Authentication authentication) {

        if (!(authentication instanceof Web3AuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
        }

        return voteService.voteReceipt((Web3AuthenticationToken) authentication)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        voteReceipt -> ResponseEntity.ok().body(voteReceipt));
    }

    @RequestMapping(value = "/receipt/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt.jwt", histogram = true)
    public ResponseEntity<?> getVoteReceipt(@PathVariable("eventId") String eventId,
                                            @PathVariable("categoryId") String categoryId,
                                            Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
        }

        var jwtAuth = (JwtAuthenticationToken) authentication;

        return voteService.voteReceipt(jwtAuth, eventId, categoryId)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        voteReceipt -> ResponseEntity.ok().body(voteReceipt));
    }

    @RequestMapping(value = "/casting-available/{eventId}/{voteId}", method = HEAD, produces = "application/json")
    @Timed(value = "resource.voteId.receipt", histogram = true)
    public ResponseEntity<?> isVoteCastingStillPossible(@PathVariable("eventId") String eventId,
                                                        @PathVariable("voteId") String voteId,
                                                        Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("JWT_REQUIRED")
                    .withDetail("JWT auth Bearer Auth token needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
        }

        var jwtAuth = (JwtAuthenticationToken) authentication;

        return voteService.isVoteCastingStillPossible(jwtAuth, eventId, voteId)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(NOT_ACCEPTABLE).build());
    }

}
