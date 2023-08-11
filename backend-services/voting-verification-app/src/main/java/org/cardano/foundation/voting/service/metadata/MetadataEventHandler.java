package org.cardano.foundation.voting.service.metadata;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MetadataEventHandler {

    @Autowired
    private CustomMetadataProcessor customMetadataProcessor;

    @Value("${l1.transaction.metadata.label}")
    private long metadataLabel;

    @EventListener
    @Transactional
    public void handleMetadataEvent(TxMetadataEvent event) {
        log.debug("Received metadata event: {}", event);
        try {
            event.getTxMetadataList().stream()
                    .filter(txMetadataLabel -> txMetadataLabel.getLabel().equalsIgnoreCase(String.valueOf(metadataLabel)))
                    .forEach(txEvent -> customMetadataProcessor.processMetadataEvent(txEvent.getSlot(), txEvent.getCbor()));
        } catch (Exception e) {
            log.warn("Error processing metadata event", e);
        }
    }

}
