package org.cardano.foundation.voting.service.voting_power;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.lovelaceToAda;

@Service
public class VotingPowerService {

    @Autowired
    private BlockchainDataStakePoolService blockchainDataStakePoolService;

    public Optional<Long> getVotingPower(Event event, String stakeAddress) {
        return switch (event.getVotingEventType()) {
            case USER_BASED -> throw new RuntimeException("voting power for USER_BASED events is not supported!");
            case STAKE_BASED -> {
                yield blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch(), stakeAddress)
                        .map(stakeAmount -> {
                            if (stakeAmount < 1_000_000L) {
                                return 0L;
                            }

                            return lovelaceToAda(BigInteger.valueOf(stakeAmount)).longValue();
                        });
            }
            case BALANCE_BASED -> {
                yield blockchainDataStakePoolService.getBalanceAmount(event.getSnapshotEpoch(), stakeAddress)
                        .map(stakeAmount -> {
                            if (stakeAmount < 1_000_000L) {
                                return 0L;
                            }

                            return lovelaceToAda(BigInteger.valueOf(stakeAmount)).longValue();
                        });
            }
        };
    }

}
