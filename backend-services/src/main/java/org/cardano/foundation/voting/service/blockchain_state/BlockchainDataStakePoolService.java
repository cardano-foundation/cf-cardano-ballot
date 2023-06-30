package org.cardano.foundation.voting.service.blockchain_state;

import java.util.Optional;

public interface BlockchainDataStakePoolService {

    Optional<Long> getStakeAmount(int epochNo, String stakeAddress);

}
