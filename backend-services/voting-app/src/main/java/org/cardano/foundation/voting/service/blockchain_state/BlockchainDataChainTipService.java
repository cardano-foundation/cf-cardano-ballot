package org.cardano.foundation.voting.service.blockchain_state;


import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.ChainTip;
import org.zalando.problem.Problem;

public interface BlockchainDataChainTipService {

    Either<Problem, ChainTip> getChainTip();

}
