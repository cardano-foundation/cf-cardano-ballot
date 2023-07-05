package org.cardano.foundation.voting.service.metadata;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadata;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataMap;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.service.cbor.CborService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.ChunkedMetadataParser;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.domain.OnChainEventType.CATEGORY_REGISTRATION;
import static org.cardano.foundation.voting.domain.OnChainEventType.EVENT_REGISTRATION;
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
    private Account organiserAccount;

    @Value("${bind.on.event.ids}")
    private List<String> bindOnEventIds;

    @Autowired
    private JsonService jsonService;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    public void processMetadataEvents(List<TransactionMetadataLabelCbor> onChainMetadataEvents) {
        log.info("On chain events:{}", onChainMetadataEvents);

        for (var onChainMetadataEvent : onChainMetadataEvents) {
            var cborBytes= decodeHexString(onChainMetadataEvent.getCborMetadata().replace("\\x", ""));

            var cborMetadata = CBORMetadata.deserialize(cborBytes);

            var envelopeCborMap = Optional.ofNullable((CBORMetadataMap) cborMetadata.get(BigInteger.valueOf(metadataLabel))).orElseThrow();

            Optional<String> maybeSignatureHexString = ChunkedMetadataParser.deChunk(envelopeCborMap.get("signature"));
            Optional<String> maybeKeyHexString = ChunkedMetadataParser.deChunk(envelopeCborMap.get("key"));
            Optional<CBORMetadataMap> maybePayloadCborMap = Optional.ofNullable(envelopeCborMap.get("payload")).map(o -> (CBORMetadataMap) o);

            if (maybeSignatureHexString.isEmpty()) {
                log.warn("Missing signature from on chain event: {}", onChainMetadataEvent);
                continue;
            }

            if (maybeKeyHexString.isEmpty()) {
                log.warn("Missing key from on chain event: {}", onChainMetadataEvent);
                continue;
            }

            if (maybePayloadCborMap.isEmpty()) {
                log.warn("Missing payload from on chain event: {}", onChainMetadataEvent);
                continue;
            }

            var maybeOnChainVotingEventType = Enums.getIfPresent(OnChainEventType.class, ((String)envelopeCborMap.get("type")));
            if (maybeOnChainVotingEventType.isEmpty()) {
                log.warn("Unknown onChainEvenType chain event type: {}", envelopeCborMap.get("type"));
                continue;
            }
            var onChainEvenType = maybeOnChainVotingEventType.orElseThrow();

            if (onChainEvenType == EVENT_REGISTRATION) {
                try {
                    processEventRegistration(maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow()).ifPresent(event -> {
                        log.info("Event registration processed: {}", event.getId());
                    });
                } catch (Exception e) {
                    log.warn("Unable to process onChainEvenType chain EVENT_REGISTRATION", e);
                }
            }
            if (onChainEvenType == CATEGORY_REGISTRATION) {
                try {
                    processCategoryRegistration(maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow()).ifPresent(category -> {
                        log.info("Category registration processed: {}", category.getId());
                    });
                } catch (Exception e) {
                    log.warn("Unable to process onChainEvenType chain CATEGORY_REGISTRATION", e);
                }
            }
        }
    }


    @SneakyThrows
    private Optional<Event> processEventRegistration(String signatureHexString, String keyHexString, CBORMetadataMap payload) {
        var id = HexUtil.encodeHexString(blake2bHash224(decodeHexString(signatureHexString)));
        log.info("Processing event registration, hash: {}", id);

        var cip30Parser = new CIP30Verifier(signatureHexString, Optional.ofNullable(keyHexString));
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

        var signaturePayloadHexString = Optional.ofNullable(cip30VerificationResult.getMessage(HEX)).orElse("");
        var payloadHexString = HexUtil.encodeHexString(Blake2bUtil.blake2bHash224(CborSerializationUtil.serialize(payload.getMap())));

        if (!signaturePayloadHexString.equals(payloadHexString)) {
            log.warn("Payload hash mismatch, signaturePayloadHexString: {}, payloadHexString:{}", signaturePayloadHexString, payloadHexString);

            return Optional.empty();
        }

        var orgAccountStakeAddress = organiserAccount.stakeAddress();
        if (!orgAccountStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", orgAccountStakeAddress, eventAddress);
            return Optional.empty();
        }

        var maybeEventRegistration = cborService.decodeEventRegistrationEnvelope(payload);
        if (maybeEventRegistration.isLeft()) {
            log.info("Event registration invalid, reason:{} :{}", maybeEventRegistration.getLeft().getDetail(), id);

            return Optional.empty();
        }
        var eventRegistration = maybeEventRegistration.get();

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
        eventRegistration.getVotingPowerAsset().ifPresent(event::setVotingPowerAsset);

        event.setVotingEventType(eventRegistration.getVotingEventType());

        eventRegistration.getStartSlot().ifPresent(event::setStartSlot);
        eventRegistration.getEndSlot().ifPresent(event::setEndSlot);

        return Optional.of(referenceDataService.storeEvent(event));
    }

    @SneakyThrows
    private Optional<Category> processCategoryRegistration(String signature, String key, CBORMetadataMap payload) {
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

        var orgAccountStakeAddress = organiserAccount.stakeAddress();
        if (!orgAccountStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", orgAccountStakeAddress, eventAddress);

            return Optional.empty();
        }

        var signaturePayloadHexString = Optional.ofNullable(cip30VerificationResult.getMessage(HEX)).orElse("");
        var payloadHexString = HexUtil.encodeHexString(Blake2bUtil.blake2bHash224(CborSerializationUtil.serialize(payload.getMap())));

        if (!signaturePayloadHexString.equals(payloadHexString)) {
            log.warn("Payload hash mismatch, signaturePayloadHexString: {}, payloadHexString:{}", signaturePayloadHexString, payloadHexString);

            return Optional.empty();
        }

        var maybeCategoryRegistration = cborService.decodeCategoryRegistrationEnvelope(payload).toJavaOptional();
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
