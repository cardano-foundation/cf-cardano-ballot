package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.service.verify.SMSUserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/user-verification")
@Slf4j
@RequiredArgsConstructor
public class UserVerificationResource {

    private final SMSUserVerificationService smsUserVerificationService;

    @RequestMapping(value = "/verified/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.isVerified", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> isVerified(@PathVariable("eventId") String eventId, @PathVariable("stakeAddress") String stakeAddress) {
        var isVerifiedRequest = new IsVerifiedRequest(stakeAddress, eventId);

        log.info("Received isVerified request: {}", isVerifiedRequest);

        return smsUserVerificationService.isVerified(isVerifiedRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        isVerifiedResponse -> {
                            return ResponseEntity.ok().body(isVerifiedResponse);
                        }
                );
    }

}
