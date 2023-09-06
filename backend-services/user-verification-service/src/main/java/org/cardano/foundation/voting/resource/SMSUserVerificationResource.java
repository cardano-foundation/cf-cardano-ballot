package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.SMSCheckVerificationRequest;
import org.cardano.foundation.voting.domain.SMSStartVerificationRequest;
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
    @Timed(value = "resource.sms.startVerification", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> startVerification(@RequestBody @Valid SMSStartVerificationRequest startVerificationRequest) {
        log.info("Received SMS startVerification request: {}", startVerificationRequest);

        return smsUserVerificationService.startVerification(startVerificationRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        userVerification -> ResponseEntity.ok().body(userVerification)
                );
    }

    @RequestMapping(value = "/check-verification", method = POST, produces = "application/json")
    @Timed(value = "resource.sms.checkVerification", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> checkVerification(@RequestBody @Valid SMSCheckVerificationRequest checkVerificationRequest) {
        log.info("Received SMS checkVerification request: {}", checkVerificationRequest);

        return smsUserVerificationService.checkVerification(checkVerificationRequest)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        userVerification -> ResponseEntity.ok().body(userVerification)
                );
    }

}
