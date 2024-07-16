package org.cardano.foundation.voting.service.common;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.cardano.foundation.voting.utils.CompletableFutures;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultUserVerificationService implements UserVerificationService {

    private final SMSUserVerificationService smsUserVerificationService;

    private final DiscordUserVerificationService discordUserVerificationService;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        log.info("Received isVerified request: {}", isVerifiedRequest);

        CompletableFuture<Either<Problem, IsVerifiedResponse>> smsVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return smsUserVerificationService.isVerified(isVerifiedRequest);
        });

        CompletableFuture<Either<Problem, IsVerifiedResponse>> discordVerificationFuture = CompletableFuture.supplyAsync(() -> {
            return discordUserVerificationService.isVerifiedBasedOnWalletId(isVerifiedRequest);
        });

        var allFutures = CompletableFutures.anyResultsOf(List.of(smsVerificationFuture, discordVerificationFuture));

        List<Either<Problem, IsVerifiedResponse>> allResponses = allFutures.orTimeout(30, SECONDS)
                .join();

        var successCount = allResponses.stream().filter(Either::isRight).count();

        if (successCount != 2) {
            var problem = allResponses.stream().filter(Either::isLeft).findFirst().orElseThrow().getLeft();

            return Either.left(problem);
        }

        var successes = allResponses.stream().filter(Either::isRight).toList().stream().map(Either::get).toList();

        var isVerified = successes.stream().reduce((a, b) -> {
            return new IsVerifiedResponse(a.isVerified() || b.isVerified());
        }).orElse(new IsVerifiedResponse(false));

        return Either.right(isVerified);
    }

}
