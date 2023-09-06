package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.zalando.problem.Problem;

import java.util.List;

public interface SMSUserVerificationService {

    Either<Problem, SMSStartVerificationResponse> startVerification(SMSStartVerificationRequest SMSStartVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(SMSCheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

    void removeUserVerification(UserVerification userVerification);

    List<UserVerification> findAllForEvent(String eventId);

    List<UserVerification> findAllPending(String eventId);

}
