package org.cardano.foundation.voting.service.blockchain_state;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class DummyBlockchainDataStakePoolService implements BlockchainDataStakePoolService {

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        var r = new Random();

        var randomStake = Math.abs(r.nextLong(45_000_000L));

        return Optional.of(randomStake);
    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        var r = new Random();

        var randomStake = Math.abs(r.nextLong(45_000_000L));

        return Optional.of(randomStake);
    }

}
