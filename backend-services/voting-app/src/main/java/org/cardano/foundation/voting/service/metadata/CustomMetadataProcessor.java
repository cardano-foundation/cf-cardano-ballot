package org.cardano.foundation.voting.service.metadata;

import co.nstant.in.cbor.CborException;
import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadata;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataMap;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.cbor.CborService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.utils.ChunkedMetadataParser;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.AddressFormat;
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
import static org.cardanofoundation.cip30.MessageFormat.HEX;

@Service
@Slf4j
public class CustomMetadataProcessor {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private CborService cborService;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Value("${bind.on.event.ids}")
    private List<String> bindOnEventIds;

    @Value("${l1.transaction.metadata.label}")
    private long metadataLabel;

    public void processMetadataEvents(List<TransactionMetadataLabelCbor> onChainMetadataEvents) {
        log.info("On chain events:{}", onChainMetadataEvents);

        for (var onChainMetadataEvent : onChainMetadataEvents) {
            try {
                processEvent(onChainMetadataEvent.getSlot(), onChainMetadataEvent.getCborMetadata(), onChainMetadataEvent.getTxHash());
            } catch (Exception e) {
                log.warn("Error processing on chain event: {}", onChainMetadataEvent, e);
            }
        }
    }

    private void processEvent(long slot, String txCbor, String txHash) throws CborException {
        var cborBytes = decodeHexString(txCbor.replace("\\x", ""));

        var cborMetadata = CBORMetadata.deserialize(cborBytes);

        var envelopeCborMap = Optional.ofNullable((CBORMetadataMap) cborMetadata.get(BigInteger.valueOf(metadataLabel))).orElseThrow();

        Optional<String> maybeSignatureHexString = ChunkedMetadataParser.deChunk(envelopeCborMap.get("signature"));
        Optional<String> maybeKeyHexString = ChunkedMetadataParser.deChunk(envelopeCborMap.get("key"));
        Optional<CBORMetadataMap> maybePayloadCborMap = Optional.ofNullable(envelopeCborMap.get("payload")).map(o -> (CBORMetadataMap) o);

        if (maybeSignatureHexString.isEmpty()) {
            log.warn("Missing signature from on chain event: {}", txCbor);
            return;
        }

        if (maybeKeyHexString.isEmpty()) {
            log.warn("Missing key from on chain event: {}", txCbor);
            return;
        }

        if (maybePayloadCborMap.isEmpty()) {
            log.warn("Missing payload from on chain event: {}", txCbor);
            return;
        }

        var maybeOnChainVotingEventType = Enums.getIfPresent(OnChainEventType.class, ((String)envelopeCborMap.get("type")));
        if (maybeOnChainVotingEventType.isEmpty()) {
            log.warn("Unknown onChainEvenType chain event type: {}", envelopeCborMap.get("type"));
            return;
        }
        var onChainEvenType = maybeOnChainVotingEventType.orElseThrow();

        if (onChainEvenType == EVENT_REGISTRATION) {
            processEventRegistration(maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow(), slot).ifPresent(event -> {
                log.info("Event registration processed: {}", event.getId());
            });
        }
        if (onChainEvenType == CATEGORY_REGISTRATION) {
            processCategoryRegistration(maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow(), slot).ifPresent(category -> {
                log.info("Category registration processed: {}", category.getId());
            });
        }
    }

    @SneakyThrows
    private Optional<Event> processEventRegistration(String signatureHexString, String keyHexString, CBORMetadataMap payload, long txSlot) {
        var id = HexUtil.encodeHexString(blake2bHash224(decodeHexString(signatureHexString)));
        log.info("Processing event registration, hash: {}", id);

        var cip30Parser = new CIP30Verifier(signatureHexString, Optional.ofNullable(keyHexString));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Signature invalid, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeEventAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);
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
        event.setCategoryResultsWhileVoting(eventRegistration.isCategoryResultsWhileVoting());
        event.setHighLevelResultsWhileVoting(eventRegistration.isHighLevelResultsWhileVoting());

        event.setVersion(SchemaVersion.fromText(eventRegistration.getSchemaVersion()).orElseThrow());

        event.setStartEpoch(eventRegistration.getStartEpoch());
        event.setEndEpoch(eventRegistration.getEndEpoch());
        event.setSnapshotEpoch(eventRegistration.getSnapshotEpoch());
        event.setVotingPowerAsset(eventRegistration.getVotingPowerAsset());

        event.setVotingEventType(eventRegistration.getVotingEventType());

        event.setStartSlot(eventRegistration.getStartSlot());
        event.setEndSlot(eventRegistration.getEndSlot());

        event.setAbsoluteSlot(txSlot);

        return Optional.of(referenceDataService.storeEvent(event));
    }

    private Optional<Category> processCategoryRegistration(String signature, String key, CBORMetadataMap payload, long txSlot) throws CborException {
        var id = HexUtil.encodeHexString(Blake2bUtil.blake2bHash224(decodeHexString(signature)));

        log.info("Processing category registration id: {}", id);

        var cip30Parser = new CIP30Verifier(signature, Optional.ofNullable(key));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Signature invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var maybeEventAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);
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
        category.setAbsoluteSlot(txSlot);
        category.setEvent(event);

        var proposals = categoryRegistration.getProposals().stream().map(proposalEnvelope -> Proposal.builder()
                .id(proposalEnvelope.getId())
                .name(proposalEnvelope.getName())
                .category(category)
                .absoluteSlot(txSlot) // same as category
                .build()
        ).toList();

        category.setProposals(proposals);

        return Optional.of(referenceDataService.storeCategory(category));
    }

}
