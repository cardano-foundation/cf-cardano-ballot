package org.cardano.foundation.voting.jobs;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataEvent;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IngestionStrategy;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.service.metadata.CustomMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProcessRecentMetadataJob implements Runnable {

    @Autowired
    private CustomMetadataService customMetadataService;

    @Value("${app.ingestion.strategy:PULL}")
    private IngestionStrategy ingestionStrategy;

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Override
    @Scheduled(fixedDelayString = "PT5M", initialDelayString = "PT0M")
    public void run() {
        switch (ingestionStrategy) {
            case PULL -> {
                log.info("Starting ProcessRecentMetadataJob...");
                customMetadataService.processRecentMetadataEvents();
                log.info("Finished ProcessRecentMetadataJob...");
            }
            case PUSH -> {
                log.info("ProcessRecentMetadataJob will not run since we actually having PUSH based ingestion strategy.");
            }
        }
    }

    // TODO job class is not ideal place for this code but it will have to do for now. We could introduce some handler package or something
    // it is good that this is in one place though...
    @EventListener
    @Transactional
    public void handleMetadataEvent(TxMetadataEvent event) {
        switch (ingestionStrategy) {
            case PUSH -> {
                log.debug("Received metadata event: {}", event);
                try {
                    var transactionMetadataLabelCbors = event.getTxMetadataList().stream()
                            .filter(txMetadataLabel -> txMetadataLabel.getLabel().equalsIgnoreCase(String.valueOf(metadataLabel)))
                            // TODO Cbor from txEvent when  yaci-store supports it
                            .map(txEvent -> new TransactionMetadataLabelCbor(txEvent.getTxHash(), txEvent.getSlot(), txEvent.getBody()))
                            .toList();

                    if (transactionMetadataLabelCbors.isEmpty()) {
                        log.debug("No metadata events with label {} found in event {}", metadataLabel, event);
                        return;
                    }

                    customMetadataService.processRecentMetadataEventsPassThrough(transactionMetadataLabelCbors);
                } catch (Exception e) {
                    log.warn("Error processing metadata event", e);
                }
            }
            case PULL -> {
                log.debug("Received metadata event: {} but will not process it since we actually having PULL based ingestion strategy.", event);
            }
        }
    }

}
