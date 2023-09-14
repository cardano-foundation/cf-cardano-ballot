package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/user-verification")
@Slf4j
@RequiredArgsConstructor
public class UserVerificationResource {

    private final SMSUserVerificationService smsUserVerificationService;

    private final DiscordUserVerificationService discordUserVerificationService;

    @RequestMapping(value = "/verified/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.isVerified", histogram = true)
    public ResponseEntity<?> isVerified(@PathVariable("eventId") String eventId,
                                        @PathVariable("stakeAddress") String stakeAddress) throws ExecutionException, InterruptedException {
        var isVerifiedRequest = new IsVerifiedRequest(stakeAddress, eventId);

        log.info("Received isVerified request: {}", isVerifiedRequest);

        CompletableFuture<Either<Problem, IsVerifiedResponse>> smsVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return smsUserVerificationService.isVerified(isVerifiedRequest);
        });

        CompletableFuture<Either<Problem, IsVerifiedResponse>> discordVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return discordUserVerificationService.isVerified(isVerifiedRequest);
        });

        var isVerified = CompletableFuture.anyOf(smsVerificationFuture, discordVerificationFuture);

        var isVerifiedResponseE = (Either<Problem, IsVerifiedResponse>) isVerified
                .orTimeout(30, SECONDS)
                .get();

        return isVerifiedResponseE.fold(problem -> {
                    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                },
                isVerifiedResponse -> {
                    return ResponseEntity.ok().body(isVerifiedResponse);
                }
        );
    }

}
