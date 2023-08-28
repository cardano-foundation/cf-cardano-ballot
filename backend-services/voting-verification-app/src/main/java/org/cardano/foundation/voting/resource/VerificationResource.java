package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VoteVerificationRequest;
import org.cardano.foundation.voting.service.verify.VoteVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/verification")
@Slf4j
@RequiredArgsConstructor
public class VerificationResource {

    private final VoteVerificationService voteVerificationService;

    @RequestMapping(value = "/verify-vote", method = POST, produces = "application/json")
    @Timed(value = "resource.verifyVote", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> verifyVote(@RequestBody @Valid VoteVerificationRequest voteVerificationRequest) {
        log.info("Received vote verification request: {}", voteVerificationRequest);

        return voteVerificationService.verifyVoteProof(voteVerificationRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        voteVerificationResult -> ResponseEntity.ok().body(voteVerificationResult)
                );
    }

    @RequestMapping(value = "/mock/verify-vote", method = POST, produces = "application/json")
    @Timed(value = "resource.mock.verifyVote", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> verifyVoteMock(@RequestBody @Valid VoteVerificationRequest voteVerificationRequest) {
        log.info("Received vote verification mock request: {}", voteVerificationRequest);

        var flap = new Random().nextBoolean();

        return ResponseEntity.ok().body(Map.of("isVerified", flap, "network", cardanoNetwork));
    }

}
