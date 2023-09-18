package org.cardano.foundation.voting.service.common;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.zalando.problem.Problem;

public interface UserVerificationService {

    Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest);

}
