package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.common.ADAConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FixedBlockchainDataStakePoolService implements BlockchainDataStakePoolService {

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(1000L).longValue());
    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        return Optional.of(ADAConversionUtil.adaToLovelace(1000).longValue());
    }

}
