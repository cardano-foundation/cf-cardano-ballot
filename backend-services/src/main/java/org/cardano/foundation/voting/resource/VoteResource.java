package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.security.JwtPrincipal;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/vote")
@Slf4j
public class VoteResource {

    @Autowired
    private VoteService voteService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @RequestMapping(value = "/cast", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.cast", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> castVote(@RequestBody CastVoteSignedWeb3Request castVoteRequest, Authentication authentication) {
        //JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();

        return voteService.castVote(castVoteRequest)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        vote -> {
                            return ResponseEntity.ok().build();
                        });
    }

    @RequestMapping(value = "/receipt/{event}/{category}/{stakeAddress}", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.receipt", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getVoteReceipt(String event, String category, String stakeAddress, Authentication authentication) {
        JwtPrincipal jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();

        if (jwtPrincipal.isNotAllowed(stakeAddress)) {
            return ResponseEntity.status(FORBIDDEN).build();
        }

        return voteService.voteReceipt(event, category, stakeAddress)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        voteReceipt -> {
                            return ResponseEntity.ok().body(voteReceipt);
                        });
    }

}
