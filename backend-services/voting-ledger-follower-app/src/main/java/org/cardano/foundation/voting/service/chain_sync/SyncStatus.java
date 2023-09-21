package org.cardano.foundation.voting.service.chain_sync;

import java.util.Optional;

public record SyncStatus(boolean isSynced, Optional<Long> diff, Optional<Exception> ex) {

    static SyncStatus ok(long diff) {
        return new SyncStatus(true, Optional.of(diff), Optional.empty());
    }

    static SyncStatus notYet(long diff) {
        return new SyncStatus(false, Optional.of(diff), Optional.empty());
    }

    static SyncStatus notYet() {
        return new SyncStatus(false, Optional.empty(), Optional.empty());
    }

    static SyncStatus error(Exception ex) {
        return new SyncStatus(false, Optional.empty(), Optional.of(ex));
    }

    static SyncStatus unknownError() {
        return new SyncStatus(false, Optional.empty(), Optional.empty());
    }

}