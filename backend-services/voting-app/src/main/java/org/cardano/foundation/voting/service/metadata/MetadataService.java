package org.cardano.foundation.voting.service.metadata;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MetadataService {

    private final static int PAGE_SIZE = 100;

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Autowired
    private BlockchainDataMetadataService blockchainDataMetadataService;

    @Autowired
    private MetadataProcessor metadataProcessor;

    @Timed(value = "resource.metadata.full.scan", percentiles = { 0.3, 0.5, 0.95 })
    public void processAllMetadataEvents() {
        boolean continueFetching = true;
        int page = 1;
        do {
            var transactionMetadataLabelCbors = blockchainDataMetadataService.fetchMetadataForLabel(String.valueOf(metadataLabel), PAGE_SIZE, page);
            if (transactionMetadataLabelCbors.size() < PAGE_SIZE) {
                continueFetching = false;
            }
            page++;

            metadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        } while (continueFetching);
    }

    @Timed(value = "resource.metadata.recent.scan", percentiles = { 0.3, 0.5, 0.95 })
    public void processRecentMetadataEvents() {
        log.info("processRecentMetadataEvents for metadata label {}", metadataLabel);
        var transactionMetadataLabelCbors = blockchainDataMetadataService.fetchMetadataForLabel(String.valueOf(metadataLabel), PAGE_SIZE, 1);
        log.info("transactionMetadataLabelCbors: {}", transactionMetadataLabelCbors);

        metadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        log.info("processRecentMetadataEvents for metadata label {} completed.", metadataLabel);
    }

}
