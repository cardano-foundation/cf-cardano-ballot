package org.cardano.foundation.voting.service.blockchain_state.yaci;

import com.bloxbean.cardano.yaci.store.blocks.service.BlockService;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.chain_sync.ChainSyncService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class YaciChainTipService implements BlockchainDataChainTipService {

    private final BlockService blockService;

    private final ChainSyncService chainSyncService;

    private final CardanoNetwork cardanoNetwork;

    private final CacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("chainTipCache")
    public Either<Problem, ChainTip> getChainTip() {
        var latestBlockM = blockService.getLatestBlock();

        if (latestBlockM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("CHAIN_TIP_NOT_FOUND")
                    .withDetail("Unable to get chain tip from backend service.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var latestBlock = latestBlockM.orElseThrow();

        var chainSync = chainSyncService.getSyncStatus(true);

        return Either.right(ChainTip.builder()
                .hash(latestBlock.getHash())
                .epochNo(Optional.ofNullable(latestBlock.getEpochNumber()).orElse(-1))
                .absoluteSlot(latestBlock.getSlot())
                .network(cardanoNetwork)
                .isSynced(chainSync.isSynced())
                .build());
    }

    @Scheduled(fixedRateString = "PT15S")
    public void evictChainTipCache() {
        cacheManager.getCache("chainTipCache")
                .clear();
    }

}
