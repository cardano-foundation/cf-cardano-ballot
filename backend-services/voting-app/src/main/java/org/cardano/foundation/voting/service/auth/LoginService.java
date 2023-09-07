package org.cardano.foundation.voting.service.auth;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.zalando.problem.Problem;

public interface LoginService {

    Either<Problem, LoginResult> login(SignedWeb3Request loginRequest);

}
