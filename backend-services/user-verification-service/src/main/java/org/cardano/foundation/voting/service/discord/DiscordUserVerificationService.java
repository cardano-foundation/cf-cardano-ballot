package org.cardano.foundation.voting.service.discord;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.zalando.problem.Problem;

public interface DiscordUserVerificationService {

    Either<Problem, DiscordStartVerificationRequest> startVerification(DiscordStartVerificationRequest startVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(DiscordCheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

    void removeUserVerification(DiscordUserVerification userVerification);

//    List<SMSUserVerification> findAllForEvent(String eventId);
//
//    List<SMSUserVerification> findAllPending(String eventId);

}
