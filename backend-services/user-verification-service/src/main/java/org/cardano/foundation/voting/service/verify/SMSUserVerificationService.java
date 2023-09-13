package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.SMSUserVerification;
import org.zalando.problem.Problem;

import java.util.List;

public interface SMSUserVerificationService {

    Either<Problem, SMSStartVerificationResponse> startVerification(SMSStartVerificationRequest SMSStartVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(SMSCheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

    void removeUserVerification(SMSUserVerification SMSUserVerification);

    List<SMSUserVerification> findAllForEvent(String eventId);

    List<SMSUserVerification> findAllPending(String eventId);

}
