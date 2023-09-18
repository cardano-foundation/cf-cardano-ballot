package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.common.UserVerificationService;
import org.cardano.foundation.voting.utils.CompletableFutures;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/user-verification")
@Slf4j
@RequiredArgsConstructor
public class UserVerificationResource {

    private final UserVerificationService userVerificationService;

    @RequestMapping(value = "/verified/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.isVerified", histogram = true)
    public ResponseEntity<?> isVerified(@PathVariable("eventId") String eventId,
                                        @PathVariable("stakeAddress") String stakeAddress) {
        var isVerifiedRequest = new IsVerifiedRequest(eventId, stakeAddress);

        return userVerificationService.isVerified(isVerifiedRequest)
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        isVerifiedResponse -> {
                            return ResponseEntity.ok().body(isVerifiedResponse);
                        }
                );
    }

}
