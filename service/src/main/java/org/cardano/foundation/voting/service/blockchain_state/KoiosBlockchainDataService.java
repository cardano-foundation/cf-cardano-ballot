package org.cardano.foundation.voting.service.blockchain_state;

import io.vavr.control.Either;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.BlockchainData;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import rest.koios.client.backend.factory.BackendFactory;
import rest.koios.client.backend.factory.BackendService;

import java.util.List;
import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static rest.koios.client.backend.factory.options.Options.EMPTY;

@Service
@Slf4j
public class KoiosBlockchainDataService implements BlockchainDataService {

    @Autowired
    private CardanoNetwork cardanoNetwork;

    private BackendService backendService;

    @PostConstruct
    public void onStart() {
        if (cardanoNetwork == CardanoNetwork.PREPROD) {
            this.backendService = BackendFactory.getKoiosPreprodService();
        }
        if (cardanoNetwork == CardanoNetwork.MAIN) {
            this.backendService = BackendFactory.getKoiosMainnetService();
        }
        if (cardanoNetwork == CardanoNetwork.DEV) {
            // TODO
        }
        if (cardanoNetwork == CardanoNetwork.PREVIEW) {
            this.backendService = BackendFactory.getKoiosPreviewService();
        }
    }

    @Override
    public Either<Problem, BlockchainData> getBlockchainData(String networkName) {
        var maybeNetwork = CardanoNetwork.fromName(networkName);
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", networkName);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return getBlockchainData(maybeNetwork.orElseThrow());
    }

    @Override
    public Either<Problem, BlockchainData> getBlockchainData(CardanoNetwork cardanoNetwork) {
        if (cardanoNetwork != this.cardanoNetwork) {
            return Either.left(Problem.builder()
                    .withTitle("WRONG_NETWORK")
                    .withDetail("Backend configured with network:" + this.cardanoNetwork)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        var blockchainData = BlockchainData.builder()
                .epochNo(getCurrentEpoch())
                .absoluteSlot(getCurrentAbsoluteSlot())
                .build();

        return Either.right(blockchainData);
    }

    @Override
    public BlockchainData getBlockchainData() {
        return getBlockchainData(this.cardanoNetwork).getOrNull();
    }

    @SneakyThrows
    private int getCurrentEpoch() {
        return backendService.getNetworkService().getChainTip().getValue().getEpochNo();
    }

    @SneakyThrows
    private long getCurrentAbsoluteSlot() {
        return backendService.getNetworkService().getChainTip().getValue().getAbsSlot().longValue();
    }

    @Override
    @SneakyThrows
    public Either<Problem, Optional<Long>> getVotingPower(CardanoNetwork cardanoNetwork, int snapshotEpochNo, String stakeAddress) {
        if (cardanoNetwork != this.cardanoNetwork) {
            return Either.left(Problem.builder()
                    .withTitle("WRONG_NETWORK")
                    .withDetail("Backend configured with network:" + this.cardanoNetwork)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        var accountHistoryResponse = backendService.getAccountService().getAccountHistory(List.of(stakeAddress), snapshotEpochNo, EMPTY);
        if (!accountHistoryResponse.isSuccessful()) {
            return Either.right(Optional.empty());
        }

        var historyList = accountHistoryResponse.getValue();

        var maybeStakeAccount = historyList.stream().filter(accountHistory -> accountHistory.getStakeAddress().equals(stakeAddress)).findFirst();

        if (maybeStakeAccount.isEmpty()) {
            log.warn("Unable to find stake account history for address: {}", stakeAddress);

            return Either.right(Optional.empty());
        }

        var stakeAccount = maybeStakeAccount.orElseThrow();

        var maybeHistoryInner = stakeAccount.getHistory().stream().filter(ah -> ah.getEpochNo() == snapshotEpochNo).findFirst();
        if (maybeHistoryInner.isEmpty()) {
            return Either.right(Optional.empty());
        }

        return Either.right(Optional.of(Long.parseLong(maybeHistoryInner.orElseThrow().getActiveStake())));
    }

}
