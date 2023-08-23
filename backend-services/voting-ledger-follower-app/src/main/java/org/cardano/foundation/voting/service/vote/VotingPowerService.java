package org.cardano.foundation.voting.service.vote;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VotingPowerService {

    @Autowired
    private BlockchainDataStakePoolService blockchainDataStakePoolService;

    public Optional<Long> getVotingPower(Event event, String stakeAddress) {
        return switch (event.getVotingEventType()) {
            case USER_BASED -> throw new RuntimeException("voting power for USER_BASED events is not supported!");
            case STAKE_BASED -> {
                yield blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress);
            }
            case BALANCE_BASED -> {
                yield blockchainDataStakePoolService.getBalanceAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress);
            }
        };
    }

}
