package org.cardano.foundation.voting.service.cbor;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;
import org.cardano.foundation.voting.domain.web3.CategoryRegistrationEnvelope;
import org.cardano.foundation.voting.domain.web3.EventRegistrationEnvelope;
import org.cardano.foundation.voting.domain.web3.ProposalEnvelope;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.cardano.foundation.voting.utils.MoreBoolean.fromInteger;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class CborService {

    public Either<Problem, CategoryRegistrationEnvelope> decodeCategoryRegistrationEnvelope(String hexString, Map payload) {
        try {
//            var cborData = CborSerializationUtil.deserialize(HexUtil.decodeHexString(hexString));
//            var cborDataMap = (Map) cborData;

            // TODO perform signature check against hash

            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, (String) payload.get("type"));

            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != OnChainEventType.CATEGORY_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeName = Optional.ofNullable((String) payload.get("name"));
            if (maybeName.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing name field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeEvent = Optional.ofNullable((String) payload.get("event"));
            if (maybeEvent.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing event field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable((Integer) payload.get("creationSlot"));
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeOptions = Optional.ofNullable((Map) payload.get("options"));
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            var maybeProposals = Optional.ofNullable((List) payload.get("proposals"));
            if (maybeProposals.isEmpty() || maybeProposals.orElseThrow().isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing proposals field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            boolean isGdprProtection = fromInteger((Integer) options.get("gdprProtection"));
            var categoryRegistration = CategoryRegistrationEnvelope.builder()
                    .type(maybeOnchainEventType.orElseThrow())
                    .name(maybeName.orElseThrow())
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

    public Either<Problem, EventRegistrationEnvelope> decodeEventRegistrationEnvelope(String hexString, Map payload) {
        try {
//            var cborData = CborSerializationUtil.deserialize(HexUtil.decodeHexString(hexString));
//            var cborDataMap = (Map) cborData;

            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, (String) payload.get("type"));
            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != OnChainEventType.EVENT_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeTeam = Optional.ofNullable((String) payload.get("team"));
            if (maybeTeam.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing team field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable((Integer) payload.get("creationSlot"));
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

            var eventRegistrationEnvelopeBuilder = EventRegistrationEnvelope.builder()
                    .name((String) payload.get("name"))
                    .type(OnChainEventType.EVENT_REGISTRATION)
                    .team(maybeTeam.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .votingEventType(votingEventType);

            if (votingEventType == VotingEventType.STAKE_BASED) {
                var maybeStartEpoch = Optional.ofNullable((Integer) payload.get("startEpoch"));
                if (maybeStartEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndEpoch = Optional.ofNullable((Integer) payload.get("endEpoch"));
                if (maybeEndEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                var maybeSnapshotEpoch = Optional.ofNullable((Integer) payload.get("snapshotEpoch"));
                if (maybeSnapshotEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing snapshotEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                eventRegistrationEnvelopeBuilder.startEpoch(maybeStartEpoch.orElseThrow());
                eventRegistrationEnvelopeBuilder.endEpoch(maybeEndEpoch.orElseThrow());
                eventRegistrationEnvelopeBuilder.snapshotEpoch(maybeSnapshotEpoch.orElseThrow());
            }

            if (votingEventType == VotingEventType.USER_BASED) {
                var maybeStartSlot = Optional.ofNullable((Integer) payload.get("startSlot"));
                if (maybeStartSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndSlot = Optional.ofNullable((Integer) payload.get("endSlot"));
                if (maybeEndSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                eventRegistrationEnvelopeBuilder.startSlot(maybeStartSlot.orElseThrow().longValue());
                eventRegistrationEnvelopeBuilder.endSlot(maybeEndSlot.orElseThrow().longValue());
            }

            var maybeOptions = Optional.ofNullable((Map) payload.get("options"));
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            eventRegistrationEnvelopeBuilder.allowVoteChanging(fromInteger((Integer) options.get("allowVoteChanging")));
            eventRegistrationEnvelopeBuilder.categoryResultsWhileVoting(fromInteger((Integer) options.get("categoryResultsWhileVoting")));
            eventRegistrationEnvelopeBuilder.schemaVersion((String) payload.get("schemaVersion"));

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

    private List<ProposalEnvelope> readProposalsEnvelope(List proposals, boolean isGdprProtection) {
        return proposals.stream().map(obj -> {
            var map = (Map) obj;

            var proposalEnvelopeBuilder = ProposalEnvelope.builder()
                    .id((String) map.get("id"));

            if (!isGdprProtection) {
                proposalEnvelopeBuilder.name((String) map.get("name"));
            }

            return proposalEnvelopeBuilder.build();
        }).toList();
    }

}
