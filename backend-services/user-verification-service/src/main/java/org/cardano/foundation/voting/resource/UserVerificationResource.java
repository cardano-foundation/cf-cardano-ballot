package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.UserVerificationRequest;
import org.cardano.foundation.voting.service.verify.UserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserVerificationResource {

    private final UserVerificationService userVerificationService;

    private final CardanoNetwork cardanoNetwork;

    @RequestMapping(value = "/verify", method = POST, produces = "application/json")
    @Timed(value = "resource.verifyUser", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> verifyUser(@RequestBody @Valid UserVerificationRequest userVerificationRequest) {
        log.info("Received user verification request: {}", userVerificationRequest);

        return userVerificationService.verify()
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        isVerified -> ResponseEntity.ok().body(Map
                                .of("isVerified", isVerified,
                                        "network", cardanoNetwork)
                        )
                );
    }

    @RequestMapping(value = "/mock/verify-user", method = POST, produces = "application/json")
    @Timed(value = "resource.mock.verifyUser", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> verifyVoteMock(@RequestBody @Valid UserVerificationRequest userVerificationRequest) {
        log.info("Received vote verification mock request: {}", userVerificationRequest);

        var flap = new Random().nextBoolean();

        return ResponseEntity.ok().body(Map.of("isVerified", flap, "network", cardanoNetwork));
    }

}
