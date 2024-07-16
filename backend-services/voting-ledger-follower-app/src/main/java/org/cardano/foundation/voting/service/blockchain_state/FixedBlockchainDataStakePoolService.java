package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.common.ADAConversionUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FixedBlockchainDataStakePoolService implements BlockchainDataStakePoolService {

    private final int amountAda;

    @PostConstruct
    public void init() {
        log.info("Initialising using FixedBlockchainDataStakePoolService.");
    }

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(amountAda).longValue());
    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(amountAda).longValue());
    }

}
