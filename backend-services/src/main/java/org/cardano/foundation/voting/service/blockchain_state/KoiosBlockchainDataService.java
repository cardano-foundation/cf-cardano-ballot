package org.cardano.foundation.voting.service.blockchain_state;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.koios.client.backend.factory.BackendFactory;
import rest.koios.client.backend.factory.BackendService;

import java.util.List;
import java.util.Optional;

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
    public ChainTip getChainTip() {
        return ChainTip.builder()
                .epochNo(getCurrentEpoch())
                .absoluteSlot(getCurrentAbsoluteSlot())
                .build();
    }

    @Override
    public long getVotingPower(CardanoNetwork network, int snapshotEpoch, String stakeAddress) {
        // TODO
        return 1L;
    }

    @Override
    public Optional<String> getLastMerkleRootHashes(Event event) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<String> getMerkleRootHashes(Event event) {
        // TODO
        return List.of();
    }

    @Override
    public Optional<TransactionDetails> getTransactionDetails(String transactionHash) {
        // TODO
        return Optional.empty();
    }

    @SneakyThrows
    private int getCurrentEpoch() {
        return backendService.getNetworkService().getChainTip().getValue().getEpochNo();
    }

    @SneakyThrows
    private long getCurrentAbsoluteSlot() {
        return backendService.getNetworkService().getChainTip().getValue().getAbsSlot().longValue();
    }

}
