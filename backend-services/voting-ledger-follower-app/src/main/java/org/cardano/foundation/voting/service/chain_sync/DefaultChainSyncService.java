package org.cardano.foundation.voting.service.chain_sync;

import com.bloxbean.cardano.client.backend.api.BackendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class DefaultChainSyncService implements ChainSyncService {

    private final BackendService orgBackendService;

    private final BackendService yaciBackendService;

    private final int chainSyncBuffer;

    private final AtomicReference<SyncStatus> syncStatusAtomic = new AtomicReference<>(SyncStatus.notYet());

    public DefaultChainSyncService(BackendService orgBackendService,
                                   BackendService yaciBackendService,
                                   int chainSyncBuffer) {
        this.orgBackendService = orgBackendService;
        this.yaciBackendService = yaciBackendService;
        this.chainSyncBuffer = chainSyncBuffer;
    }

    @Scheduled(fixedDelayString = "${chain.sync.status.check.interval:PT30S}", initialDelayString = "${chain.sync.status.check.initial.delay:PT5S}")
    public void updateSyncStatus() {
        SyncStatus status = fetchSyncStatus();

        syncStatusAtomic.set(status);

        log.info("Updated chain sync status: {}", status);

        if (status.isSynced()) {
            log.info("Yaci is synced with the original chain. Diff: {} (slots)", status.diff().orElse(-1L));
        } else {
            log.warn("Yaci is not synced with the original chain. Diff: {} (slots)", status.diff().orElse(-1L));
        }
    }

    @Override
    public SyncStatus getSyncStatus(boolean cached) {
        if (cached) {
            return syncStatusAtomic.get();
        }

        return fetchSyncStatus();
    }

    public SyncStatus fetchSyncStatus() {
        try {
            var orgLastBlockResult = orgBackendService.getBlockService().getLatestBlock();
            var yaciLastBlockResult = yaciBackendService.getBlockService().getLatestBlock();

            if (orgLastBlockResult.isSuccessful() && yaciLastBlockResult.isSuccessful()) {
                var diff = orgLastBlockResult.getValue().getSlot() - yaciLastBlockResult.getValue().getSlot();

                log.info("Current diff: {} (slots) between org and yaci.", diff);

                var diffAbs = Math.abs(diff);

                boolean isSynced = diffAbs <= chainSyncBuffer;

                if (isSynced) {
                    return SyncStatus.ok(diff);
                }

                log.warn("Yaci is not synced with the original chain. Diff: {} (slots)", diff);

                return SyncStatus.notYet(diff);
            }

            return SyncStatus.unknownError();
        } catch (Exception e) {
            log.error("Backend service is not available: {}", e.getMessage());

            return SyncStatus.error(e);
        }
    }



}
