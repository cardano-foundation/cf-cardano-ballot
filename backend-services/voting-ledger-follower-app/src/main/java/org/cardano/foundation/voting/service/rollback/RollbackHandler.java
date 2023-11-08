package org.cardano.foundation.voting.service.rollback;

import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.utxo.EventResultsUtxoDataService;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class RollbackHandler {

    private final EventResultsUtxoDataService eventResultsUtxoDataService;
    private final ReferenceDataService referenceDataService;
    private final MerkleRootHashService merkleRootHashService;

    @EventListener
    @Transactional
    public void handleRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback, event:{}", rollbackEvent);

        long rollbackToSlot = rollbackEvent.getRollbackTo().getSlot();

        eventResultsUtxoDataService.rollbackAfterSlot(rollbackToSlot);
        merkleRootHashService.rollbackAfterSlot(rollbackToSlot);
        referenceDataService.rollbackReferenceDataAfterSlot(rollbackToSlot);

        log.info("Rollbacked handled.");
    }

}
