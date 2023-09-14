package org.cardano.foundation.voting.service.discord;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.cardano.foundation.voting.repository.DiscordUserVerificationRepository;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

@Service
@Slf4j
@AllArgsConstructor
public class DefaultDiscordUserVerificationService implements DiscordUserVerificationService {

    private final DiscordUserVerificationRepository userVerificationRepository;

    @Override
    public Either<Problem, DiscordStartVerificationRequest> startVerification(DiscordStartVerificationRequest startVerificationRequest) {
        return null;
    }

    @Override
    public Either<Problem, IsVerifiedResponse> checkVerification(DiscordCheckVerificationRequest checkVerificationRequest) {
        return null;
    }

    @Override
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        isVerifiedRequest.
    }

    @Override
    public void removeUserVerification(DiscordUserVerification userVerification) {

    }

}
