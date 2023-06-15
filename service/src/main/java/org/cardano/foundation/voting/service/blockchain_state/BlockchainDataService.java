package org.cardano.foundation.voting.service.blockchain_state;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.BlockchainData;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.zalando.problem.Problem;

import java.util.Optional;

public interface BlockchainDataService {

    Either<Problem, BlockchainData> getBlockchainData(CardanoNetwork cardanoNetwork);

    BlockchainData getBlockchainData();

    Either<Problem, BlockchainData> getBlockchainData(String network);

    /**
     * Get Voting Power as lovelaces
     *
     * @param stakeAddress
     * @return
     */
    Either<Problem, Optional<Long>> getVotingPower(CardanoNetwork cardanoNetwork, int snapshotEpochNo, String stakeAddress);

}
