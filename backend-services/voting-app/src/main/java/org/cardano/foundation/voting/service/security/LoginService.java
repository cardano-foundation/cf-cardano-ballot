package org.cardano.foundation.voting.service.security;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.zalando.problem.Problem;

public interface LoginService {

    Either<Problem, String> login(SignedWeb3Request loginRequest);

    //Either<Problem, Boolean> checkTokenExpiration(String accessToken);

}
