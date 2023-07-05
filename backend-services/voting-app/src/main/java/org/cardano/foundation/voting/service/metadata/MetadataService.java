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
    private int metadataLabel;

    @Autowired
    private BlockchainDataMetadataService blockchainDataMetadataService;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private MetadataProcessor metadataProcessor;

    @Timed(value = "resource.metadata.full.scan", percentiles = { 0.3, 0.5, 0.95 })
    public void processAllMetadataEvents() {
        boolean continueFetching = true;
        int page = 1;
        do {
            String metadataLabelString = String.valueOf(metadataLabel);
            var transactionMetadataLabelCbors = blockchainDataMetadataService.fetchMetadataForLabel(metadataLabelString, PAGE_SIZE, page);
            if (transactionMetadataLabelCbors.size() < PAGE_SIZE) {
                continueFetching = false;
            }
            page++;

            // MetadataToJsonNoSchemaConverter.

            //var onchainMetadataEvents = storeMetadataEvents(metadataLabelString, transactionMetadataLabelCbors);
            metadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        } while (continueFetching);
    }

    @Timed(value = "resource.metadata.recent.scan", percentiles = { 0.3, 0.5, 0.95 })
    public void processRecentMetadataEvents() {
        log.info("processRecentMetadataEvents for metadata label {}", metadataLabel);
        var jsons = blockchainDataMetadataService.fetchMetadataForLabel(String.valueOf(metadataLabel), PAGE_SIZE, 1);
        log.info("{}", jsons);

        //var onchainMetadataEvents = storeMetadataEvents(String.valueOf(metadataLabel), jsons);
        metadataProcessor.processMetadataEvents(jsons);

        log.info("processRecentMetadataEvents for metadata label {} completed.", metadataLabel);
    }

//    private List<OnchainMetadata> storeMetadataEvents(String metadataLabelString, List<String> metadataBodyAsJsonList) {
//        var events = new ArrayList<OnchainMetadata>();
//
//        for (var metadataBody : metadataBodyAsJsonList) {
//            try {
//                var metadataBodyNode = JsonUtil.parseJson(metadataBody);
//                var onChainEventTypeString = metadataBodyNode.get("type").asText();
//                log.info("metadata body:{}", metadataBody);
//                var maybeOnChainEventType = Enums.getIfPresent(OnChainEventType.class, onChainEventTypeString);
//                if (maybeOnChainEventType.isEmpty()) {
//                    log.warn("Unrecognised event: {}", onChainEventTypeString);
//                    continue;
//                }
//                var onChainEventType = maybeOnChainEventType.orElseThrow();
//
//                var signature = parseArrayStringMetadata(metadataBodyNode.get("signature"));
//                var key = parseArrayStringMetadata(metadataBodyNode.get("key"));
//
//                var id = encodeHexString(sha2_256(metadataBody));
//
//                var onChainMetadata = OnchainMetadata.builder()
//                        .id(id)
//                        .onChainEventType(onChainEventType)
//                        .metadataLabel(metadataLabelString)
//                        .signature(signature)
//                        .key(key)
//                        .build();
//
//                if (metadataRepository.findById(id).isPresent()) {
//                    log.info("Metadata already exists: {}, ignoring", id);
//                    continue;
//                }
//
//                events.add(metadataRepository.save(onChainMetadata));
//            } catch (JsonProcessingException e) {
//                log.error("Unable to parse metadataBody: {}", metadataBody, e);
//            }
//        }
//
//        return events;
//    }

}
