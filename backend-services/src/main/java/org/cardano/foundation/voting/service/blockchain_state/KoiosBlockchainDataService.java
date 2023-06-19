package org.cardano.foundation.voting.service.blockchain_state;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.BlockchainData;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.koios.client.backend.factory.BackendFactory;
import rest.koios.client.backend.factory.BackendService;

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
    public BlockchainData getBlockchainData() {
        return BlockchainData.builder()
                .epochNo(getCurrentEpoch())
                .absoluteSlot(getCurrentAbsoluteSlot())
                .build();
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
