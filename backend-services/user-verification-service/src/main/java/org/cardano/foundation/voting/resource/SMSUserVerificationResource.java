package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CheckVerificationRequest;
import org.cardano.foundation.voting.domain.StartVerificationRequest;
import org.cardano.foundation.voting.service.verify.SMSUserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/sms/user-verification")
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

}
