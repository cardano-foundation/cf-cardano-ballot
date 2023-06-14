package org.cardano.foundation.voting.service;

import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.BlockchainData;
import org.cardano.foundation.voting.domain.Network;
import org.zalando.problem.Problem;

import java.util.Optional;

public interface BlockchainDataService {

    Either<Problem, BlockchainData> getBlockchainData(Network network);

    Either<Problem, BlockchainData> getBlockchainData(String network);

    /**
     * Get Voting Power as lovelaces
     *
     * @param stakeAddress
     * @return
     */
    Either<Problem, Optional<Long>> getVotingPower(Network network, int snapshotEpochNo, String stakeAddress);

}
