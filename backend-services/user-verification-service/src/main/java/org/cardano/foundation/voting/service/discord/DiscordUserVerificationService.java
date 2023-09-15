package org.cardano.foundation.voting.service.discord;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationResponse;
import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.zalando.problem.Problem;

import java.util.List;

public interface DiscordUserVerificationService {

    Either<Problem, DiscordStartVerificationResponse> startVerification(String eventId, DiscordStartVerificationRequest startVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(String eventId, DiscordCheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerifiedBasedOnStakeAddress(IsVerifiedRequest isVerifiedRequest);

    Either<Problem, IsVerifiedResponse> isVerifiedBasedOnDiscordIdHash(String eventId, String discordIdHash);

    void removeUserVerification(DiscordUserVerification userVerification);

    List<DiscordUserVerification> findAllForEvent(String eventId);

    List<DiscordUserVerification> findAllPending(String eventId);

}
