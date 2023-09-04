package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.*;
import org.zalando.problem.Problem;

public interface SMSUserVerificationService {

    Either<Problem, StartVerificationResponse> startVerification(StartVerificationRequest startVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(CheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

}
