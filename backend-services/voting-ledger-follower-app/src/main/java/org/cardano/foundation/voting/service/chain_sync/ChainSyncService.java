package org.cardano.foundation.voting.service.chain_sync;

public interface ChainSyncService {

    SyncStatus getSyncStatus(boolean cached);

    static class Noop implements ChainSyncService {

        @Override
        public SyncStatus getSyncStatus(boolean cached) {
            return SyncStatus.ok(0);
        }
    }

}
