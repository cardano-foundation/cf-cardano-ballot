package org.cardano.foundation.voting.service;

import java.util.Optional;

public interface BlockchainDataService {

    int getCurrentEpoch();

    long getCurrentSlot();

    /**
     * Get Voting Power as lovelaces
     *
     * @param stakeAddress
     * @return
     */
    Optional<Long> getVotingPower(String stakeAddress);

}


// -1000 slot + 1000