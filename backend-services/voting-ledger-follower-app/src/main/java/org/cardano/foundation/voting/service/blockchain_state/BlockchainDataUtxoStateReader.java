package org.cardano.foundation.voting.service.blockchain_state;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Utxo;
import org.zalando.problem.Problem;

import java.util.List;

public interface BlockchainDataUtxoStateReader {

    Either<Problem, List<Utxo>> getUTxOs(String address);

}
