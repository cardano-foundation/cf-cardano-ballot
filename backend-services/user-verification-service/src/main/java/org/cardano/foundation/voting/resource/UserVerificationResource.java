package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
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

    private final SMSUserVerificationService smsUserVerificationService;

    private final DiscordUserVerificationService discordUserVerificationService;

    @RequestMapping(value = "/verified/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.isVerified", histogram = true)
    public ResponseEntity<?> isVerified(@PathVariable("eventId") String eventId,
                                        @PathVariable("stakeAddress") String stakeAddress) {
        var isVerifiedRequest = new IsVerifiedRequest(eventId, stakeAddress);

        log.info("Received isVerified request: {}", isVerifiedRequest);

        CompletableFuture<Either<Problem, IsVerifiedResponse>> smsVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return smsUserVerificationService.isVerified(isVerifiedRequest);
        });

        CompletableFuture<Either<Problem, IsVerifiedResponse>> discordVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return discordUserVerificationService.isVerifiedBasedOnStakeAddress(isVerifiedRequest);
        });

        var allFutures = CompletableFutures.anyResultsOf(List.of(smsVerificationFuture, discordVerificationFuture));

        List<Either<Problem, IsVerifiedResponse>> allResponses = allFutures.orTimeout(30, SECONDS)
                .join();

        var successCount = allResponses.stream().filter(Either::isRight).count();

        if (successCount != 2) {
            var problem = allResponses.stream().filter(Either::isLeft).findFirst().orElseThrow().getLeft();

            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
        }

        var successes = allResponses.stream().filter(Either::isRight).toList().stream().map(Either::get).toList();

        var isVerified = successes.stream().reduce((a, b) -> {
            return new IsVerifiedResponse(a.isVerified() || b.isVerified());
        }).orElse(new IsVerifiedResponse(false));

        return ResponseEntity.ok().body(isVerified);
    }

}
