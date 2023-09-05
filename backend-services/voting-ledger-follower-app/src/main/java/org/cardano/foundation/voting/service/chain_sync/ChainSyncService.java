package org.cardano.foundation.voting.service.chain_sync;

import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ChainSyncService {

    @Autowired
    @Qualifier("original_blockfrost")
    private BFBackendService orgBackendService;

    @Autowired
    @Qualifier("yaci_blockfrost")
    private BFBackendService yaciBackendService;

    @Value("${chain.sync.buffer:5}")
    private final int chainSyncBuffer = 5;

    public SyncStatus getSyncStatus() {
        try {
            var orgLastBlockResult = orgBackendService.getBlockService().getLatestBlock();
            var yaciLastBlockResult = yaciBackendService.getBlockService().getLatestBlock();

            if (orgLastBlockResult.isSuccessful() && yaciLastBlockResult.isSuccessful()) {
                long diff = orgLastBlockResult.getValue().getSlot() - yaciLastBlockResult.getValue().getSlot();

                log.info("Current diff: {} (slots) between org and yaci.", diff);

                boolean isSynced = diff >= 0 && diff <= chainSyncBuffer;

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

    public record SyncStatus(boolean isSynced, Optional<Long> diff, Optional<Exception> ex) {

        static SyncStatus ok(long diff) {
            return new SyncStatus(true, Optional.of(diff), Optional.empty());
        }

        static SyncStatus notYet(long diff) {
            return new SyncStatus(false, Optional.of(diff), Optional.empty());
        }

        static SyncStatus error(Exception ex) {
            return new SyncStatus(false, Optional.empty(), Optional.of(ex));
        }

        static SyncStatus unknownError() {
            return new SyncStatus(false, Optional.empty(), Optional.empty());
        }

    }

}
