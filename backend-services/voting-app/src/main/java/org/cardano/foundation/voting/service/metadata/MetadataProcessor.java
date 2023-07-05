package org.cardano.foundation.voting.service.metadata;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadata;
import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;
import org.cardano.foundation.voting.service.cbor.CborService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.domain.metadata.OnChainEventType.CATEGORY_REGISTRATION;
import static org.cardano.foundation.voting.domain.metadata.OnChainEventType.EVENT_REGISTRATION;
import static org.cardano.foundation.voting.utils.ChunkedMetadataParser.parseArrayStringMetadata;
import static org.cardanofoundation.cip30.Format.HEX;

@Service
@Slf4j
public class MetadataProcessor {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private CborService cborService;

    @Autowired
    @Qualifier("organiser_account")
    private Account account;

    @Value("${bind.on.event.ids}")
    private List<String> bindOnEventIds;

    @Autowired
    private JsonService jsonService;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    public void processMetadataEvents(List<TransactionMetadataLabelCbor> onChainMetadataEvents) {
        log.info("On chain events:{}", onChainMetadataEvents);

        for (var onChainMetadataEvent : onChainMetadataEvents) {
            log.info(onChainMetadataEvent.getCborMetadata());
            var cbormetadataasBytes= decodeHexString(onChainMetadataEvent.getCborMetadata().replace("\\x", ""));

            var onchainEventJsonNodeE = jsonService.decode(CBORMetadata.deserialize(cbormetadataasBytes).toJson());
            if (onchainEventJsonNodeE.isEmpty()) {
                continue;
            }
            if (!onchainEventJsonNodeE.get().has(String.valueOf(metadataLabel))) {
                continue;
            }

            var onchainEventJsonNode = onchainEventJsonNodeE.get().get(String.valueOf(metadataLabel));

            var maybeOnChainVotingEventType = Enums.getIfPresent(OnChainEventType.class, onchainEventJsonNode.get("type").asText());
            if (maybeOnChainVotingEventType.isEmpty()) {
                log.warn("Unknown onChainEvenType chain event maybeOnChainVotingEventType: {}", onchainEventJsonNode.get("type").asText());
                continue;
            }
            var onChainEvenType = maybeOnChainVotingEventType.orElseThrow();

            var maybeSignature = parseArrayStringMetadata(onchainEventJsonNode.get("signature"));
            var maybeKey = parseArrayStringMetadata(onchainEventJsonNode.get("key"));
            var maybePayload =  Optional.ofNullable(onchainEventJsonNode.get("payload"));

            if (maybeSignature.isEmpty() || maybeKey.isEmpty() || maybePayload.isEmpty()) {
                log.warn("Missing signature, key or payload from on chain event: {}", onChainMetadataEvent);
                continue;
            }

            var signature = maybeSignature.get();
            var key = maybeKey.get();
            var payload = maybePayload.get();

            if (onChainEvenType == EVENT_REGISTRATION) {
                try {
                    processEventRegistration(signature, key, payload).ifPresent(event -> {
                        log.info("Event registration processed: {}", event.getId());
                    });
                } catch (Exception e) {
                    log.warn("Unable to process onChainEvenType chain EVENT_REGISTRATION", e);
                }
            }
            if (onChainEvenType == CATEGORY_REGISTRATION) {
                try {
                    processCategoryRegistration(signature, key, payload).ifPresent(category -> {
                        log.info("Category registration processed: {}", category.getId());
                    });
                } catch (Exception e) {
                    log.warn("Unable to process onChainEvenType chain CATEGORY_REGISTRATION", e);
                }
            }
        }
    }

    private Optional<Event> processEventRegistration(String signature, String key, JsonNode payload) {
        var id = HexUtil.encodeHexString(blake2bHash224(decodeHexString(signature)));
        log.info("Processing event registration, hash: {}", id);

        var cip30Parser = new CIP30Verifier(signature, Optional.ofNullable(key));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Signature invalid, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeEventAddress = cip30VerificationResult.getAddress().flatMap(addrBytes -> Bech32.decode(addrBytes).toJavaOptional());
        if (maybeEventAddress.isEmpty()) {
            log.info("Address not found or invalid, ignoring id:{}", id);

            return Optional.empty();
        }
        var eventAddress = maybeEventAddress.orElseThrow();
        log.info("eventAddress:{}", eventAddress);

        String blake2bPayloadHash = cip30VerificationResult.getMessage(HEX);

        var orgAccountStakeAddress = account.stakeAddress();
        if (!orgAccountStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", orgAccountStakeAddress, eventAddress);
            return Optional.empty();
        }

        var maybeEventRegistration = cborService.decodeEventRegistrationEnvelope(blake2bPayloadHash, payload).toJavaOptional();
        if (maybeEventRegistration.isEmpty()) {
            log.info("Event registration invalid, ignoring id:{}", id);

            return Optional.empty();
        }
        var eventRegistration = maybeEventRegistration.orElseThrow();

        if (!bindOnEventIds.contains(eventRegistration.getName())) {
            log.info("Event NOT found in bindOnEventIds, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeStoredEvent = referenceDataService.findEventByName(eventRegistration.getName());
        if (maybeStoredEvent.isPresent()) {
            log.info("Event already found, ignoring id:{}", id);

            return Optional.empty();
        }

        var event = new Event();
        event.setId(eventRegistration.getName());
        event.setVersion(event.getVersion());
        event.setTeam(eventRegistration.getTeam());
        event.setVersion(SchemaVersion.fromText(eventRegistration.getSchemaVersion()).orElseThrow());

        eventRegistration.getStartEpoch().ifPresent(event::setStartEpoch);
        eventRegistration.getEndEpoch().ifPresent(event::setEndEpoch);
        eventRegistration.getSnapshotEpoch().ifPresent(event::setSnapshotEpoch);

        event.setVotingEventType(eventRegistration.getVotingEventType());

        eventRegistration.getStartSlot().ifPresent(event::setStartSlot);
        eventRegistration.getEndSlot().ifPresent(event::setEndSlot);

        return Optional.of(referenceDataService.storeEvent(event));
    }

    private Optional<Category> processCategoryRegistration(String signature, String key, JsonNode payload) {
        var id = HexUtil.encodeHexString(Blake2bUtil.blake2bHash224(decodeHexString(signature)));

        log.info("Processing category registration id: {}", id);

        var cip30Parser = new CIP30Verifier(signature, Optional.ofNullable(key));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Signature invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var maybeEventAddress = cip30VerificationResult.getAddress().flatMap(addrBytes -> Bech32.decode(addrBytes).toJavaOptional());
        if (maybeEventAddress.isEmpty()) {
            log.info("Address not found or invalid, ignoring id: {}", id);
            return Optional.empty();
        }
        var eventAddress = maybeEventAddress.orElseThrow();

        var orgAccountStakeAddress = account.stakeAddress();
        if (!orgAccountStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", orgAccountStakeAddress, eventAddress);

            return Optional.empty();
        }

        String hexString = cip30VerificationResult.getMessage(HEX);

        var maybeCategoryRegistration = cborService.decodeCategoryRegistrationEnvelope(hexString, payload).toJavaOptional();
        if (maybeCategoryRegistration.isEmpty()) {
            log.info("Category registration invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var categoryRegistration = maybeCategoryRegistration.orElseThrow();

        if (!bindOnEventIds.contains(categoryRegistration.getEvent())) {
            log.info("Event in category NOT found in bindOnEventIds, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeStoredEvent = referenceDataService.findEventByName(categoryRegistration.getEvent());
        if (maybeStoredEvent.isEmpty()) {
            log.info("Event not found, ignoring category registration ignoring id: {}", id);

            return Optional.empty();
        }
        var event = maybeStoredEvent.orElseThrow();

        var maybeCategory = referenceDataService.findCategoryByName(categoryRegistration.getName());
        if (maybeCategory.isPresent()) {
            log.info("Category already found, ignoring name: {}", categoryRegistration.getName());

            return Optional.empty();
        }

        var category = new Category();
        category.setId(categoryRegistration.getName());
        category.setVersion(SchemaVersion.fromText(categoryRegistration.getSchemaVersion()).orElseThrow());
        category.setGdprProtection(categoryRegistration.isGdprProtection());
        category.setEvent(event);

        var proposals = categoryRegistration.getProposals().stream().map(proposalEnvelope -> Proposal.builder()
                .id(proposalEnvelope.getId())
                .name(proposalEnvelope.getName())
                .category(category)
                .build()
        ).toList();

        category.setProposals(proposals);

        return Optional.of(referenceDataService.storeCategory(category));
    }

}
