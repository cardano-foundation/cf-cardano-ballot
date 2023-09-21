package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Account;
import org.zalando.problem.Problem;

public interface AccountService {

    Either<Problem, Account> findAccount(String eventName, String stakeAddress);

}
