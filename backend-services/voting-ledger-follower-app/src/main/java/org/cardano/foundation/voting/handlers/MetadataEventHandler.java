package org.cardano.foundation.voting.handlers;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataEvent;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.metadata.CustomMetadataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MetadataEventHandler {

    @Autowired
    private CustomMetadataProcessor customMetadataProcessor;

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @EventListener
    @Async("singleThreadExecutor")
    public void handleMetadataEvent(TxMetadataEvent event) {
        log.debug("Received metadata event: {}", event);

        try {
            var txMetadataList = event.getTxMetadataList();
            for (var txEvent : txMetadataList) {
                if (txEvent.getLabel().equalsIgnoreCase(String.valueOf(metadataLabel))) {
                    customMetadataProcessor.processMetadataEvent(txEvent.getSlot(), txEvent.getCbor());
                }
            }
        } catch (Exception e) {
            log.warn("Error processing metadata event, cause:{}", e.getMessage());
        }
    }

}
