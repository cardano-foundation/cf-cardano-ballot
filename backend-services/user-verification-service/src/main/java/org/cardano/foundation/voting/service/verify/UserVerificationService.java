package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

public interface UserVerificationService {

    Either<Problem, StartVerificationResponse> startVerification(StartVerificationRequest startVerificationRequest);

    Either<Problem, IsVerifiedResponse> checkVerification(CheckVerificationRequest checkVerificationRequest);

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

}
