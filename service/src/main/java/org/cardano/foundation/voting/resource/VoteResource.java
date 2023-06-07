package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.cardano.foundation.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
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
    public ResponseEntity<?> castVote(CastVoteRequest castVoteRequest) {
        var maybeEvent = referenceDataService.findEvent(castVoteRequest.getSignedVote().getEventId());
        if (maybeEvent.isEmpty()) {
            // TODO make a better error e.g. using Zalando Problem
            // https://github.com/zalando/problem
            return ResponseEntity.notFound().build();
        }
        var event = maybeEvent.get();

        // TODO
        // check if vote has been cast
        // check if vote is valid
        // check if vote is valid for the event
        // check if vote is valid for the category

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/receipt", method = GET, produces = "application/json")
    @Timed(value = "resource.vote.receipt", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getVoteReceipt(VoteReceiptRequest voteReceiptRequest) {
        // check if vote has been cast
        // check if there is a basic receipt

        return ResponseEntity.ok(BasicVoteReceipt.builder().build());
    }

    @RequestMapping(value = "/verify", method = POST, produces = "application/json")
    @Timed(value = "resource.vote.verify", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> validateVote(VerifyVoteRequest verifyVoteRequest) {
        // given verifyVoteRequest that contains merkle proof, we verify against our merkle root hash if this vote is valid

        return ResponseEntity.ok(CompleteVoteReceipt.builder().build());
    }

}
