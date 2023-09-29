package org.cardano.foundation.voting.service.blockchain_state.backend_bridge;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class BackendServiceBlockchainDataCurrentStakePoolService implements BlockchainDataStakePoolService {

    private final BackendService backendService;

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        var accountService = backendService.getAccountService();

        try {
            var accountInfoResult = accountService.getAccountInformation(stakeAddress);
            if (accountInfoResult.isSuccessful()) {
                var accountInfo = accountInfoResult.getValue();

                if (!accountInfo.getActive()) {
                    return Optional.empty();
                }

                return Optional.of(Long.parseLong(accountInfo.getControlledAmount()));
            }

            log.warn("Failed to get stake amount for epoch {} and stake address {}.", epochNo, stakeAddress);

            return Optional.empty();
        } catch (ApiException e) {
            // TODO: handle 404 vs 5xx

            log.warn("Failed to get stake amount for epoch {} and stake address {}", epochNo, stakeAddress, e);

            return Optional.empty();
        }

    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        throw new RuntimeException("blockfrost doesn't support such endpoint");
    }

}
