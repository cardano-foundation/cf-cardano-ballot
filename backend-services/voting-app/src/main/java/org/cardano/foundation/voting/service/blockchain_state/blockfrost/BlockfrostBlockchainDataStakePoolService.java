package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.exception.APIException;
import io.blockfrost.sdk.api.model.AccountHistory;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;

import java.util.List;
import java.util.Optional;

public class BlockfrostBlockchainDataStakePoolService extends AbstractBlockfrostService implements BlockchainDataStakePoolService {

    @Override
    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
        try {
            int page = 1;
            do {
                var accountHistory = this.accountService.getAccountHistory(stakeAddress, 100, page);

                var maybeStakeAmount = getStakeAmount(epochNo, accountHistory);
                if (maybeStakeAmount.isPresent()) {
                    var stakeAmount = maybeStakeAmount.orElseThrow();

                    return Optional.of(stakeAmount);
                }
                if (accountHistory.size() < 100) {
                    return maybeStakeAmount;
                }
                page++;
            } while (true);
        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return Optional.empty();
            }
        }

        throw new IllegalStateException("Failed to get stake amount for epoch " + epochNo + " and stake address " + stakeAddress);
    }

    private static Optional<Long> getStakeAmount(int epochNo, List<AccountHistory> accountHistory) {
        return accountHistory.stream()
                .filter(accountHistoryItem -> accountHistoryItem.getActiveEpoch() == epochNo)
                .findFirst()
                .map(ah -> Long.parseLong(ah.getAmount()));
    }

}
