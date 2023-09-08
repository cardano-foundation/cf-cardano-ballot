package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.auth.JwtAuthenticationToken;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/vote")
@Slf4j
@RequiredArgsConstructor
public class VoteResource {

    private final VoteService voteService;

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.cast", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> castVote(@RequestBody @Valid SignedWeb3Request castVoteRequest) {
        log.info("Casting vote: {}", castVoteRequest);

        var startStop = new StopWatch();
        startStop.start();

        return voteService.castVote(castVoteRequest)
                .fold(problem -> {
                            startStop.stop();

                            log.warn("Vote cast failed: {}, running time:{} secs.", problem, startStop.getTotalTimeSeconds());

                            return ResponseEntity
                                    .status(Objects.requireNonNull(problem.getStatus()).getStatusCode())
                                    .body(problem);
                        },
                        vote -> {
                            startStop.stop();

                            log.info("Vote cast: {}, running time:{} secs.", vote, startStop.getTotalTimeSeconds());

                            return ResponseEntity.ok().build();
                        });
    }

    @RequestMapping(value = "/receipt", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.receipt.web3", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getVoteReceipt(@RequestBody @Valid SignedWeb3Request viewVoteReceiptRequest) {
        return voteService.voteReceipt(viewVoteReceiptRequest)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(Objects.requireNonNull(problem.getStatus()).getStatusCode())
                                    .body(problem);
                        },
                        voteReceipt -> {
                            return ResponseEntity.ok().body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/receipt/{eventId}/{categoryId}", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt.jwt", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getVoteReceipt(@PathVariable("eventId") String eventId,
                                            @PathVariable("categoryId") String categoryId,
                                            Authentication authentication) {
        var jwtAuth = (JwtAuthenticationToken) authentication;

        return voteService.voteReceipt(jwtAuth, eventId, categoryId)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(Objects.requireNonNull(problem.getStatus()).getStatusCode())
                                    .body(problem);
                        },
                        voteReceipt -> {
                            return ResponseEntity.ok().body(voteReceipt);
                        });
    }

    @RequestMapping(value = "/casting-available/{eventId}/{voteId}", method = GET, produces = "application/json")
    @Timed(value = "resource.voteId.receipt", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> isVoteCastingStillPossible(@PathVariable("eventId") String eventId,
                                                        @PathVariable String voteId) {
        return voteService.isVoteCastingStillPossible(eventId, voteId)
                .fold(problem -> ResponseEntity
                        .status(Objects.requireNonNull(problem.getStatus()).getStatusCode())
                        .body(problem),
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(NOT_ACCEPTABLE).build());
    }



}
