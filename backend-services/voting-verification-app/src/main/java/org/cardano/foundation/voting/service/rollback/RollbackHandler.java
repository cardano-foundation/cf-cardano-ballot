package org.cardano.foundation.voting.service.rollback;

import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RollbackHandler {

    @Autowired
    private MerkleRootHashRepository merkleRootHashRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    @EventListener
    @Transactional
    public void handleRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback, event:{}", rollbackEvent);

        long rollbackToSlot = rollbackEvent.getRollbackTo().getSlot();

        referenceDataService.rollbackReferenceDataAfterSlot(rollbackToSlot);

        merkleRootHashRepository.deleteAllAfterSlot(rollbackToSlot);

        log.info("Rollbacked handled.");
    }

}
