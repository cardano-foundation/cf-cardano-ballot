package org.cardano.foundation.voting.service;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Network;
import org.springframework.stereotype.Service;
import rest.koios.client.backend.factory.BackendFactory;
import rest.koios.client.backend.factory.BackendService;

import java.util.List;
import java.util.Optional;

import static rest.koios.client.backend.factory.options.Options.EMPTY;

@Service
@Slf4j
public class KoiosBlockchainDataService implements BlockchainDataService {

    // TODO move to the config
    private Network network = Network.PREPROD;

    private BackendService backendService;

    @PostConstruct
    public void onStart() {
        if (network == Network.PREPROD) {
            this.backendService = BackendFactory.getKoiosPreprodService();
        }
    }

    @Override
    @SneakyThrows
    public int getCurrentEpoch(Network network) {
        if (network != this.network) {
            throw new IllegalArgumentException("Backend connected to different network!");
        }

        return backendService.getEpochService().getLatestEpochInfo().getValue().getEpochNo();
    }

    @Override
    @SneakyThrows
    public long getCurrentAbsoluteSlot(Network network) {
        if (network != this.network) {
            throw new IllegalArgumentException("Backend connected to different network!");
        }

        return backendService.getBlockService().getLatestBlock().getValue().getAbsSlot().longValue();
    }

    @Override
    @SneakyThrows
    public Optional<Long> getVotingPower(Network network, int snapshotEpochNo, String stakeAddress) {
        if (network != this.network) {
            throw new IllegalArgumentException("Backend connected to different network!");
        }

        var accountHistoryResponse = backendService.getAccountService().getAccountHistory(List.of(stakeAddress), snapshotEpochNo, EMPTY);
        if (!accountHistoryResponse.isSuccessful()) {
            return Optional.empty();
        }

        var historyList = accountHistoryResponse.getValue();

        var stakeAccount = historyList.stream().filter(accountHistory -> accountHistory.getStakeAddress().equals(stakeAddress)).findFirst();

        if (stakeAccount.isEmpty()) {
            log.warn("Unable to find stake account history for address: {}", stakeAddress);

            return Optional.empty();
        }

        var stakeAcc = stakeAccount.orElseThrow();

        var historyInner = stakeAcc.getHistory().stream().filter(ah -> ah.getEpochNo() == snapshotEpochNo).findFirst();
        if (historyInner.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(Long.parseLong(historyInner.orElseThrow().getActiveStake()));
    }

}
