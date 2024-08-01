package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.WalletType;
import org.zalando.problem.Problem;

public interface AccountService {

    Either<Problem, Account> findAccount(String eventName, WalletType walletType, String walletId);

}
