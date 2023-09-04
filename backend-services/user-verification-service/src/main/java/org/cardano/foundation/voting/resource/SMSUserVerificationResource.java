package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CheckVerificationRequest;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.StartVerificationRequest;
import org.cardano.foundation.voting.service.verify.SMSUserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/user-verification/sms")
@Slf4j
@RequiredArgsConstructor
public class SMSUserVerificationResource {

    private final SMSUserVerificationService smsUserVerificationService;

    @RequestMapping(value = "/start-verification", method = POST, produces = "application/json")
    @Timed(value = "resource.startVerification", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> startVerification(@RequestBody @Valid StartVerificationRequest startVerificationRequest) {
        log.info("Received startVerification request: {}", startVerificationRequest);

        return smsUserVerificationService.startVerification(startVerificationRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        userVerification -> ResponseEntity.ok().body(userVerification)
                );
    }

    @RequestMapping(value = "/check-verification", method = POST, produces = "application/json")
    @Timed(value = "resource.checkVerification", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> checkVerification(@RequestBody @Valid CheckVerificationRequest checkVerificationRequest) {
        log.info("Received checkVerification request: {}", checkVerificationRequest);

        return smsUserVerificationService.checkVerification(checkVerificationRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        userVerification -> ResponseEntity.ok().body(userVerification)
                );
    }

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
