package org.cardano.foundation.voting.service.blockchain_state;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.model.AccountHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class BackendServiceBlockchainDataStakePoolService implements BlockchainDataStakePoolService {

    private final BackendService backendService;

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        var accountService = backendService.getAccountService();

        try {
            int page = 1;
            do {
                var accountHistoryResult = accountService.getAccountHistory(stakeAddress, 100, page);
                if (accountHistoryResult.isSuccessful()) {
                    var accountHistory = accountHistoryResult.getValue();

                    var maybeStakeAmount = getStakeAmount(epochNo, accountHistory);
                    if (maybeStakeAmount.isPresent()) {
                        long stakeAmount = maybeStakeAmount.orElseThrow();

                        return Optional.of(stakeAmount);
                    }
                    if (accountHistory.size() < 100) {
                        return maybeStakeAmount;
                    }
                    page++;
                }

                log.warn("Failed to get stake amount for epoch {} and stake address {}, page {}", epochNo, stakeAddress, page);
            } while (true);
        } catch (ApiException e) {
            // TODO: handle 404 vs 5xx

            log.error("Failed to get stake amount for epoch {} and stake address {}", epochNo, stakeAddress, e);

            return Optional.empty();
        }

    }

    @Override
    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
        throw new RuntimeException("blockfrost doesn't support such endpoint");
    }

    private static Optional<Long> getStakeAmount(int epochNo, List<AccountHistory> accountHistory) {
        return accountHistory.stream()
                .filter(accountHistoryItem -> accountHistoryItem.getActiveEpoch() == epochNo)
                .findFirst()
                .map(ah -> Long.parseLong(ah.getAmount()));
    }

}
