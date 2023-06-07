package org.cardano.foundation.voting.service;

import java.security.SecureRandom;
import java.util.Optional;

public class StakingService {

    // TODO this call could be moved to another microservice
    public Optional<Long> votingPower(String stakingAddress) {
        // make http call to blockfrost to deliver the data

        return Optional.of(new SecureRandom().nextLong());
    }

}
