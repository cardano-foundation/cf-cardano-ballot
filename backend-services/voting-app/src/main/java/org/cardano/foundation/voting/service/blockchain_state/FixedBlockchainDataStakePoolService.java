package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.common.ADAConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FixedBlockchainDataStakePoolService implements BlockchainDataStakePoolService {

    private final int amountAda;

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(amountAda).longValue());
    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(amountAda).longValue());
    }

}
