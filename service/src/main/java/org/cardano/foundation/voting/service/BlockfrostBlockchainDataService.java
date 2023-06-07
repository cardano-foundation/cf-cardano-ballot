package org.cardano.foundation.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class BlockfrostBlockchainDataService implements BlockchainDataService {

    private String blockfrostKey; // TODO move to config

    @Autowired
    private HttpClient httpClient;

    @Override
    public int getCurrentEpoch() {
        return 415;
    }

    @Override
    public long getCurrentSlot() {
        return 0;
    }

    @Override
    public Optional<Long> getVotingPower(String stakeAddress) {
        return Optional.of(new SecureRandom().nextLong());
    }

}
