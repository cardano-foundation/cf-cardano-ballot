package org.cardano.foundation.voting.service.verify;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

@Service
@Slf4j
public class UserVerificationService {

    public Either<Problem, Boolean> verify() {
        return Either.right(true);
    }

}
