package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Account;
import org.zalando.problem.Problem;

import java.util.Optional;

public interface AccountService {
    Either<Problem, Optional<Account>> findAccount(String networkName, String eventName, String stakeAddress);
}
