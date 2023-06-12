package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.domain.Network;

import java.util.Optional;

public interface BlockchainDataService {

    int getCurrentEpoch(Network network);

    long getCurrentAbsoluteSlot(Network network);

    /**
     * Get Voting Power as lovelaces
     *
     * @param stakeAddress
     * @return
     */
    Optional<Long> getVotingPower(Network network, int snapshotEpochNo, String stakeAddress);

}
