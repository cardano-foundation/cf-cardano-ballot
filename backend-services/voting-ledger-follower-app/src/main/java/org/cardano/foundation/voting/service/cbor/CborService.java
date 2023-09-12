package org.cardano.foundation.voting.service.cbor;

import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataList;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataMap;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.cardano.foundation.voting.domain.web3.CategoryRegistrationEnvelope;
import org.cardano.foundation.voting.domain.web3.CommitmentsEnvelope;
import org.cardano.foundation.voting.domain.web3.EventRegistrationEnvelope;
import org.cardano.foundation.voting.domain.web3.ProposalEnvelope;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.OnChainEventType.COMMITMENTS;
import static org.cardano.foundation.voting.domain.OnChainEventType.EVENT_REGISTRATION;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.utils.MoreBoolean.fromBigInteger;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class CborService {

    private final Environment environment;

    public Either<Problem, CommitmentsEnvelope> decodeCommitmentsEnvelope(CBORMetadataMap payload) {
        try {
            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, (String) payload.get("type"));

            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != COMMITMENTS) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_COMMITMENTS")
                                .withDetail("Invalid commitments event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable(payload.get("creationSlot")).map(obj -> ((BigInteger) obj).longValue());
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_COMMITMENTS_EVENT")
                                .withDetail("Invalid commitments event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCommitments = Optional.ofNullable(payload.get("commitments")).map(obj -> (CBORMetadataMap) obj);
            if (maybeCommitments.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_COMMITMENTS_EVENT")
                                .withDetail("Invalid category registration event, missing actual commitments field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var commitments = maybeCommitments.orElseThrow();

            var commitmentsEnvelope = CommitmentsEnvelope.builder()
                    .type(maybeOnchainEventType.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .schemaVersion((String) payload.get("schemaVersion"))
                    .build();

            for (Object eventId : commitments.keys()) {
                var commitmentMap = (CBORMetadataMap) commitments.get((String) eventId);
                var commitmentHash = (String) commitmentMap.get("hash");
                commitmentsEnvelope.addCommitment((String) eventId, commitmentHash);
            }

            return Either.right(commitmentsEnvelope);
        } catch (Exception e) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_COMMITMENTS_EVENT")
                            .withDetail("Invalid commitments event")
                            .withStatus(INTERNAL_SERVER_ERROR)
                            .withDetail(e.getMessage())
                            .build()
            );
        }
    }

    public Either<Problem, CategoryRegistrationEnvelope> decodeCategoryRegistrationEnvelope(CBORMetadataMap payload) {
        try {
            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, (String) payload.get("type"));

            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != OnChainEventType.CATEGORY_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeName = Optional.ofNullable((String) payload.get("id"));
            if (maybeName.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing name field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            log.info("Category registration event name:{}", maybeName.orElseThrow());

            var maybeEvent = Optional.ofNullable((String) payload.get("event"));
            if (maybeEvent.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing event field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable(payload.get("creationSlot")).map(obj -> ((BigInteger) obj).longValue());
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeOptions = Optional.ofNullable(payload.get("options")).map(obj -> (CBORMetadataMap) obj);
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            var maybeProposals = Optional.ofNullable(payload.get("proposals")).map(obj -> (CBORMetadataList) obj);
            if (maybeProposals.isEmpty() || maybeProposals.orElseThrow().size() == 0) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing proposals field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            boolean isGdprProtection = fromBigInteger((BigInteger) options.get("gdprProtection")).orElse(false);
            var categoryRegistration = CategoryRegistrationEnvelope.builder()
                    .type(maybeOnchainEventType.orElseThrow())
                    .id(maybeName.orElseThrow())
                    .event(maybeEvent.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .gdprProtection(isGdprProtection)
                    .proposals(readProposalsEnvelope(maybeProposals.orElseThrow(), isGdprProtection))
                    .schemaVersion((String) payload.get("schemaVersion"))
                    .build();

            return Either.right(categoryRegistration);
        } catch (Exception e) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CATEGORY_EVENT")
                            .withDetail("Invalid category event")
                            .withStatus(INTERNAL_SERVER_ERROR)
                            .withDetail(e.getMessage())
                            .build()
            );
        }
    }

    public Either<Problem, EventRegistrationEnvelope> decodeEventRegistrationEnvelope(CBORMetadataMap payload) {
        try {
            String name = (String) payload.get("id");

            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, (String) payload.get("type"));
            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != EVENT_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeOrganisers = Optional.ofNullable((String)payload.get("organisers"));
            if (maybeOrganisers.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing organisers field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable(payload.get("creationSlot")).map(obj -> ((BigInteger) obj).longValue());
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeVotingEventType = Enums.getIfPresent(VotingEventType.class, (String) payload.get("votingEventType"));
            if (maybeVotingEventType.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing votingEventType field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var votingEventType = maybeVotingEventType.orElseThrow();

            var maybeVotingPowerAsset = Enums.getIfPresent(VotingPowerAsset.class, (String) payload.get("votingPowerAsset"));
            if (List.of(STAKE_BASED, BALANCE_BASED).contains(votingEventType)) {
                if (maybeVotingPowerAsset.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing votingPowerAsset field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
            }

            var eventRegistrationEnvelopeBuilder = EventRegistrationEnvelope.builder()
                    .name(name)
                    .type(EVENT_REGISTRATION)
                    .organisers(maybeOrganisers.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .votingPowerAsset(maybeVotingPowerAsset)
                    .votingEventType(votingEventType);

            if (List.of(STAKE_BASED, BALANCE_BASED).contains(votingEventType)) {
                var maybeStartEpoch = Optional.ofNullable(payload.get("startEpoch")).map(obj -> ((BigInteger) obj).intValue());

                if (maybeStartEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndEpoch = Optional.ofNullable(payload.get("endEpoch")).map(obj -> ((BigInteger) obj).intValue());
                if (maybeEndEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                var maybeSnapshotEpoch = Optional.ofNullable(payload.get("snapshotEpoch")).map(obj -> ((BigInteger) obj).intValue());
                if (maybeSnapshotEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing snapshotEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                var maybeProposalsRevealEpoch = Optional.ofNullable(payload.get("proposalsRevealEpoch")).map(obj -> ((BigInteger) obj).intValue());

                if (maybeProposalsRevealEpoch.isEmpty()) {
                    log.info("proposalsRevealEpoch for event:{} is not available, setting it to endEpoch", name);

                    maybeProposalsRevealEpoch = Optional.of(maybeEndEpoch.orElseThrow());
                } else {
                    log.info("proposalsRevealEpoch for event:{} is available, setting it to {}", name, maybeProposalsRevealEpoch.orElseThrow());
                }

                if (maybeStartEpoch.orElseThrow() > maybeEndEpoch.orElseThrow()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, startEpoch must be less than endEpoch.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                if (!isYaciLocalDevNet()) {
                    if (maybeSnapshotEpoch.orElseThrow() >= maybeStartEpoch.orElseThrow()) {
                        return Either.left(
                                Problem.builder()
                                        .withTitle("INVALID_EVENT_REGISTRATION")
                                        .withDetail("Invalid event registration event, snapshotEpoch must be less than startEpoch.")
                                        .withStatus(BAD_REQUEST)
                                        .build()
                        );
                    }
                }

                if (maybeProposalsRevealEpoch.orElseThrow() < maybeEndEpoch.orElseThrow()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, proposalsRevealEpoch must be greater than equal endEpoch.")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                eventRegistrationEnvelopeBuilder.startEpoch(maybeStartEpoch);
                eventRegistrationEnvelopeBuilder.endEpoch(maybeEndEpoch);
                eventRegistrationEnvelopeBuilder.snapshotEpoch(maybeSnapshotEpoch);
                eventRegistrationEnvelopeBuilder.proposalsRevealEpoch(maybeProposalsRevealEpoch);
            }

            if (votingEventType == USER_BASED) {
                var maybeStartSlot = Optional.ofNullable(payload.get("startSlot")).map(obj -> ((BigInteger) obj).longValue());
                if (maybeStartSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndSlot = Optional.ofNullable(payload.get("endSlot")).map(obj -> ((BigInteger) obj).longValue());
                if (maybeEndSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                var maybeProposalsRevealSlot = Optional.ofNullable(payload.get("proposalsRevealSlot")).map(obj -> ((BigInteger) obj).longValue());

                if (maybeProposalsRevealSlot.isEmpty()) {
                    log.info("proposalsRevealSlot for event:{} is not available, setting it to endSlot", name);

                    maybeProposalsRevealSlot = Optional.of(maybeEndSlot.orElseThrow());
                } else {
                    log.info("proposalsRevealSlot for event:{} is available, setting it to {}", name, maybeProposalsRevealSlot.orElseThrow());
                }

                if (maybeEndSlot.orElseThrow() < maybeStartSlot.orElseThrow()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, startSlot must be less than endSlot.")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                if (maybeProposalsRevealSlot.orElseThrow() < maybeEndSlot.orElseThrow()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, proposalsRevealSlot must be greater than equal endSlot.")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                eventRegistrationEnvelopeBuilder.startSlot(maybeStartSlot);
                eventRegistrationEnvelopeBuilder.endSlot(maybeEndSlot);
                eventRegistrationEnvelopeBuilder.proposalsRevealSlot(maybeProposalsRevealSlot);
            }

            var maybeOptions = Optional.ofNullable((CBORMetadataMap) payload.get("options"));
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            eventRegistrationEnvelopeBuilder.allowVoteChanging(fromBigInteger(((BigInteger)options.get("allowVoteChanging"))).orElse(false));

            eventRegistrationEnvelopeBuilder.highLevelEventResultsWhileVoting(fromBigInteger(((BigInteger)options.get("highLevelEventResultsWhileVoting"))).orElse(false));
            eventRegistrationEnvelopeBuilder.highLevelCategoryResultsWhileVoting(fromBigInteger(((BigInteger)options.get("highLevelCategoryResultsWhileVoting"))).orElse(false));
            eventRegistrationEnvelopeBuilder.categoryResultsWhileVoting(fromBigInteger(((BigInteger)options.get("categoryResultsWhileVoting"))).orElse(false));

            eventRegistrationEnvelopeBuilder.schemaVersion((String)payload.get("schemaVersion"));

            return Either.right(eventRegistrationEnvelopeBuilder.build());
        } catch (Exception e) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_EVENT_REGISTRATION")
                            .withDetail("Invalid event registration event")
                            .withStatus(INTERNAL_SERVER_ERROR)
                            .withDetail(e.getMessage())
                            .build());

        }
    }

    private static List<ProposalEnvelope> readProposalsEnvelope(CBORMetadataList proposalsNode,
                                                                boolean isGdprProtection) {
        var proposals = new ArrayList<ProposalEnvelope>();

        for (int i = 0; i < proposalsNode.size(); i++) {
            var proposalCborMap = (CBORMetadataMap) proposalsNode.getValueAt(i);

            var proposalEnvelopeBuilder = ProposalEnvelope.builder()
                    .id((String) proposalCborMap.get("id"));

            if (!isGdprProtection) {
                proposalEnvelopeBuilder.name((String) proposalCborMap.get("name"));
            }

            proposals.add(proposalEnvelopeBuilder.build());
        }

        return proposals;
    }

    private boolean isYaciLocalDevNet() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev--yaci-dev-kit");
    }

}
