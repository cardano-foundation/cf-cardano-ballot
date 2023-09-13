package org.cardano.foundation.voting.service.auth;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.zalando.problem.Problem;

public interface LoginService {

    Either<Problem, LoginResult> login(Web3AuthenticationToken web3AuthenticationToken);

}
