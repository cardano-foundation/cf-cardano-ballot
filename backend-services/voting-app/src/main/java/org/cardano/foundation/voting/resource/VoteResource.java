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
    @Timed(value = "resource.vote.cast", histogram = true)
    public ResponseEntity<?> castVote(Authentication authentication) {
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
        var jwtAuth = (JwtAuthenticationToken) authentication;

        return voteService.voteReceipt(jwtAuth, eventId, categoryId)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        voteReceipt -> ResponseEntity.ok().body(voteReceipt));
    }

    @RequestMapping(value = "/casting-available/{eventId}/{voteId}", method = GET, produces = "application/json")
    @Timed(value = "resource.voteId.receipt", histogram = true)
    public ResponseEntity<?> isVoteCastingStillPossible(@PathVariable("eventId") String eventId,
                                                        @PathVariable("voteId") String voteId) {
        return voteService.isVoteCastingStillPossible(eventId, voteId)
                .fold(problem -> ResponseEntity
                        .status(problem.getStatus().getStatusCode())
                        .body(problem),
                        isAvailable -> isAvailable ? ResponseEntity.ok().build() : ResponseEntity.status(NOT_ACCEPTABLE).build());
    }

}
