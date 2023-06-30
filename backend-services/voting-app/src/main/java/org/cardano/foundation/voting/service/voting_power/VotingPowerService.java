package org.cardano.foundation.voting.service.voting_power;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VotingPowerService {

    @Autowired
    private BlockchainDataStakePoolService blockchainDataStakePoolService;

    public long getVotingPower(Event event, String stakeAddress) {
        return switch (event.getVotingEventType()) {
            case USER_BASED -> 1L;
            case STAKE_BASED -> blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch(), stakeAddress).orElse(0L);
        };
    }

}
