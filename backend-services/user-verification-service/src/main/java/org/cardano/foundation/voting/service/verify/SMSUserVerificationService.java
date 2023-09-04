package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.zalando.problem.Problem;

import java.util.List;

public interface SMSUserVerificationService {

    Either<Problem, StartVerificationResponse> startVerification(StartVerificationRequest startVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(CheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

    void removeUserVerification(UserVerification userVerification);

    List<UserVerification> findAllForEvent(String eventId);

    List<UserVerification> findAllPending(String eventId);

}
