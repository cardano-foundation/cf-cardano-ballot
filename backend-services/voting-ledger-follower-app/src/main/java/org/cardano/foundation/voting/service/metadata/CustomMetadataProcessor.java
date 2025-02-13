package org.cardano.foundation.voting.service.metadata;

import co.nstant.in.cbor.CborException;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadata;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataMap;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.HydraTally;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.domain.entity.*;
import org.cardano.foundation.voting.domain.web3.HydraTallyRegistrationEnvelope;
import org.cardano.foundation.voting.domain.web3.TallyRegistrationEnvelope;
import org.cardano.foundation.voting.service.cbor.CborService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.cardano.foundation.voting.utils.ChunkedMetadataParser;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash224;
import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;
import static org.cardano.foundation.voting.domain.OnChainEventType.*;
import static org.cardano.foundation.voting.domain.entity.Tally.TallyType.HYDRA;
import static org.cardanofoundation.cip30.AddressFormat.TEXT;
import static org.cardanofoundation.cip30.MessageFormat.HEX;

@Service
@Slf4j
@Primary
public class CustomMetadataProcessor {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private MerkleRootHashService merkleRootHashService;

    @Autowired
    private CborService cborService;

    @Value("${bind.on.event.ids}")
    private List<String> bindOnEventIds;

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Value("${organiser.account.stakeAddress}")
    private String organiserStakeAddress;

    @Transactional
    public void processMetadataEvent(long slot, String txCbor) throws CborException {
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
            processEventRegistration(slot, maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow()).ifPresent(event -> {
                log.info("Event registration processed: {}", event.getId());
            });
        }
        if (onChainEvenType == CATEGORY_REGISTRATION) {
            processCategoryRegistration(slot, maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow()).ifPresent(category -> {
                log.info("Category registration processed: {}", category.getId());
            });
        }
        if (onChainEvenType == COMMITMENTS) {
            processCommitments(slot, maybeSignatureHexString.orElseThrow(), maybeKeyHexString.orElseThrow(), maybePayloadCborMap.orElseThrow()).ifPresent(merkleRootHashes -> {
                log.info("On chain commitments processed: {}", merkleRootHashes);
            });
        }
    }

    private Optional<Event> processEventRegistration(long slot,
                                                     String signatureHexString,
                                                     String keyHexString,
                                                     CBORMetadataMap payload) throws CborException {
        var id = encodeHexString(blake2bHash224(decodeHexString(signatureHexString)));
        log.info("Processing event registration, hash: {}", id);

        var cip30Parser = new CIP30Verifier(signatureHexString, Optional.ofNullable(keyHexString));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Event registration signature invalid, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeEventAddress = cip30VerificationResult.getAddress(TEXT);
        if (maybeEventAddress.isEmpty()) {
            log.info("Address not found or invalid, ignoring id:{}", id);

            return Optional.empty();
        }
        var eventAddress = maybeEventAddress.orElseThrow();
        log.info("eventAddress:{}", eventAddress);

        var signaturePayloadHexString = Optional.ofNullable(cip30VerificationResult.getMessage(HEX)).orElse("");
        var payloadHexString = encodeHexString(blake2bHash224(CborSerializationUtil.serialize(payload.getMap())));

        if (!signaturePayloadHexString.equals(payloadHexString)) {
            log.warn("Payload hash mismatch, signaturePayloadHexString: {}, payloadHexString:{}", signaturePayloadHexString, payloadHexString);

            return Optional.empty();
        }

        if (!organiserStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", organiserStakeAddress, eventAddress);

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

        var eventM = referenceDataService.findEventByName(eventRegistration.getName());

        if (eventM.isPresent()) {
            var event = eventM.orElseThrow();

            log.info("Event already found, will be removing old one and re-creating it, id:{}", event.getId());

            referenceDataService.removeEvent(event);
        }

        var event = new Event();
        event.setId(eventRegistration.getName());
        event.setVersion(event.getVersion());
        event.setOrganisers(eventRegistration.getOrganisers());

        event.setHighLevelEventResultsWhileVoting(Optional.of(eventRegistration.isHighLevelEventResultsWhileVoting()));
        event.setHighLevelCategoryResultsWhileVoting(Optional.of(eventRegistration.isHighLevelCategoryResultsWhileVoting()));
        event.setCategoryResultsWhileVoting(Optional.of(eventRegistration.isCategoryResultsWhileVoting()));
        event.setVersion(eventRegistration.getSchemaVersion());

        event.setStartEpoch(eventRegistration.getStartEpoch());
        event.setEndEpoch(eventRegistration.getEndEpoch());
        event.setSnapshotEpoch(eventRegistration.getSnapshotEpoch());
        event.setVotingPowerAsset(eventRegistration.getVotingPowerAsset());

        event.setVotingEventType(eventRegistration.getVotingEventType());

        event.setStartSlot(eventRegistration.getStartSlot());
        event.setEndSlot(eventRegistration.getEndSlot());

        event.setProposalsRevealEpoch(eventRegistration.getProposalsRevealEpoch());
        event.setProposalsRevealSlot(eventRegistration.getProposalsRevealSlot());

        event.setAbsoluteSlot(slot);

        var tallies = eventRegistration.getTallies()
                .stream()
                .map(tally -> {
                    var tallyBuilder = Tally.builder()
                            .name(tally.getName())
                            .type(tally.getType())
                            .description(tally.getDescription());

                    if (tally.getType() == HYDRA) {
                        tallyBuilder.hydraTallyConfig(createTallyConfig(tally));
                    }

                    return tallyBuilder.build();
                }
        ).toList();

        event.getTallies().clear();
        event.getTallies().addAll(tallies);

        return Optional.of(referenceDataService.storeEvent(event));
    }

    private static HydraTally createTallyConfig(TallyRegistrationEnvelope tally) {
        var tallyConfig = (HydraTallyRegistrationEnvelope) tally.getConfig();

        return HydraTally.builder()
                .contractName(tallyConfig.getContractName())
                .contractDescription(tallyConfig.getContractDesc())
                .contractVersion(tallyConfig.getContractVersion())
                .compiledScript(tallyConfig.getCompiledScript())
                .compiledScriptHash(tallyConfig.getCompiledScriptHash())
                .compilerName(tallyConfig.getCompilerName())
                .compilerVersion(tallyConfig.getCompilerVersion())
                .plutusVersion(tallyConfig.getPlutusVersion())
                .verificationKeyHashes(String.join(":", tallyConfig.getVerificationKeys()))
                .build();
    }

    private Optional<Category> processCategoryRegistration(long slot,
                                                           String signature,
                                                           String key,
                                                           CBORMetadataMap payload) throws CborException {
        var id = encodeHexString(blake2bHash224(decodeHexString(signature)));

        log.info("Processing category registration id: {}", id);

        var cip30Parser = new CIP30Verifier(signature, Optional.ofNullable(key));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Category registration signature invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var maybeEventAddress = cip30VerificationResult.getAddress(TEXT);
        if (maybeEventAddress.isEmpty()) {
            log.info("Address not found or invalid, ignoring id: {}", id);
            return Optional.empty();
        }
        var eventAddress = maybeEventAddress.orElseThrow();

        if (!organiserStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", organiserStakeAddress, eventAddress);

            return Optional.empty();
        }

        var signaturePayloadHexString = Optional.ofNullable(cip30VerificationResult.getMessage(HEX)).orElse("");
        var payloadHexString = encodeHexString(blake2bHash224(CborSerializationUtil.serialize(payload.getMap())));

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
        log.info(categoryRegistration.toString());

        if (!bindOnEventIds.contains(categoryRegistration.getEvent())) {
            log.info("Event in category NOT found in bindOnEventIds, ignoring id:{}", id);

            return Optional.empty();
        }

        var maybeStoredEvent = referenceDataService.findEventByName(categoryRegistration.getEvent());
        if (maybeStoredEvent.isEmpty()) {
            log.warn("Event not found, category registration id: {}", id);

            return Optional.empty();
        }

        var eventM = referenceDataService.findEventByName(categoryRegistration.getEvent());

        if (eventM.isEmpty()) {
            log.warn("Category registration failed, event not found, category registration id: {}", id);

            return Optional.empty();
        }

        var categoryM = referenceDataService.findCategoryByName(categoryRegistration.getId());


        if (categoryM.isPresent()) {
            var category = categoryM.orElseThrow();

            log.info("Category already found, will be removing old one and re-creating it, id: {}", category.getId());

            referenceDataService.removeCategory(category);
        }

        var category = new Category();
        category.setId(categoryRegistration.getId());
        category.setVersion(categoryRegistration.getSchemaVersion());
        category.setGdprProtection(categoryRegistration.isGdprProtection());
        category.setAbsoluteSlot(slot);
        category.setEvent(eventM.orElseThrow());

        var proposals = categoryRegistration.getProposals().stream().map(proposalEnvelope -> {
                    return Proposal.builder()
                            .id(proposalEnvelope.getId())
                            .name(proposalEnvelope.getName())
                            .category(category)
                            .absoluteSlot(slot)
                            .build();
                }
        ).toList();

        category.getProposals().clear();
        category.getProposals().addAll(proposals);

        return Optional.of(referenceDataService.storeCategory(category));
    }

    private Optional<List<MerkleRootHash>> processCommitments(long slot,
                                                              String signature,
                                                              String key,
                                                              CBORMetadataMap payload) throws CborException {
        var id = encodeHexString(blake2bHash224(decodeHexString(signature)));

        log.info("Processing on-chain commitments: {}", id);

        var cip30Parser = new CIP30Verifier(signature, Optional.ofNullable(key));
        var cip30VerificationResult = cip30Parser.verify();
        if (!cip30VerificationResult.isValid()) {
            log.info("Signature invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var maybeEventAddress = cip30VerificationResult.getAddress(TEXT);
        if (maybeEventAddress.isEmpty()) {
            log.info("Address not found or invalid, ignoring id: {}", id);
            return Optional.empty();
        }
        var eventAddress = maybeEventAddress.orElseThrow();

        if (!organiserStakeAddress.equals(eventAddress)) {
            log.warn("Addresses mismatch, orgAccountStakeAddress: {}, eventAddress:{}", organiserStakeAddress, eventAddress);

            return Optional.empty();
        }

        var signaturePayloadHexString = Optional.ofNullable(cip30VerificationResult.getMessage(HEX)).orElse("");
        var payloadHexString = encodeHexString(blake2bHash224(CborSerializationUtil.serialize(payload.getMap())));

        if (!signaturePayloadHexString.equals(payloadHexString)) {
            log.warn("Payload hash mismatch, signaturePayloadHexString: {}, payloadHexString:{}", signaturePayloadHexString, payloadHexString);

            return Optional.empty();
        }

        var maybeCommitmentsEnvelope = cborService.decodeCommitmentsEnvelope(payload).toJavaOptional();
        if (maybeCommitmentsEnvelope.isEmpty()) {
            log.info("Category registration invalid, ignoring id: {}", id);

            return Optional.empty();
        }
        var commitmentsEnvelope = maybeCommitmentsEnvelope.orElseThrow();

        var relevantCommitments = findOutRelevantEvents(commitmentsEnvelope.getCommitments());

        var merkleRootHashes = new ArrayList<MerkleRootHash>();
        for (var eventId : relevantCommitments.keySet()) {
            var maybeStoredEvent = referenceDataService.findEventByName(eventId);
            if (maybeStoredEvent.isEmpty()) {
                log.info("Event not found, ignoring commitment, id: {}", eventId);
                continue;
            }

            var maybeEventCommitment = commitmentsEnvelope.getCommitment(eventId);
            if (maybeEventCommitment.isEmpty()) {
                log.info("Commitment not found, ignoring commitment, id: {}", eventId);
                continue;
            }

            merkleRootHashes.add(MerkleRootHash.builder()
                    .eventId(eventId)
                    .merkleRootHash(maybeEventCommitment.orElseThrow())
                    .absoluteSlot(slot)
                    .build()
            );
        }

        if (merkleRootHashes.isEmpty()) {
            //log.info("No actual commitments (merkle root hashes) found in the actual on-chain COMMITMENTS event.");

            return Optional.empty();
        }

        return Optional.of(merkleRootHashService.storeCommitments(merkleRootHashes));
    }

    /**
     * We are only interested in commitments based on events we are binding into
     */
    private Map<String, Map<String, String>> findOutRelevantEvents(Map<String, Map<String, String>> commitments) {
        return commitments.entrySet()
                .stream()
                .filter(entry -> bindOnEventIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
