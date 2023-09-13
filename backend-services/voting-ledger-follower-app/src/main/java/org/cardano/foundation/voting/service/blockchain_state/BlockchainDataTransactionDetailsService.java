package org.cardano.foundation.voting.service.blockchain_state;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.zalando.problem.Problem;

import java.util.Optional;

public interface BlockchainDataTransactionDetailsService {

    default Either<Problem, Optional<TransactionDetails>> getTransactionDetails(String transactionHash) {
        return Either.right(Optional.empty());
    }

}
