package org.cardano.foundation.voting.service.cbor;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.utils.MoreBoolean.fromInteger;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class CborService {

    public Either<Problem, CategoryRegistrationEnvelope> decodeCategoryRegistrationEnvelope(String hexString, JsonNode payload) {
        try {
            // TODO perform signature check against hash

            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, payload.get("type").asText());

            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != OnChainEventType.CATEGORY_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeName = Optional.ofNullable(payload.get("name").asText());
            if (maybeName.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing name field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeEvent = Optional.ofNullable(payload.get("event").asText());
            if (maybeEvent.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing event field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable(payload.get("creationSlot")).map(JsonNode::asLong);
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeOptions = Optional.ofNullable(payload.get("options"));
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            var maybeProposals = Optional.ofNullable(payload.get("proposals"));
            if (maybeProposals.isEmpty() || maybeProposals.orElseThrow().isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_CATEGORY_REGISTRATION")
                                .withDetail("Invalid category registration event, missing proposals field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            boolean isGdprProtection = fromInteger(options.get("gdprProtection").asInt());
            var categoryRegistration = CategoryRegistrationEnvelope.builder()
                    .type(maybeOnchainEventType.orElseThrow())
                    .name(maybeName.orElseThrow())
                    .event(maybeEvent.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .gdprProtection(isGdprProtection)
                    .proposals(readProposalsEnvelope(maybeProposals.orElseThrow(), isGdprProtection))
                    .schemaVersion(payload.get("schemaVersion").asText())
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

    public Either<Problem, EventRegistrationEnvelope> decodeEventRegistrationEnvelope(String hexString, JsonNode payload) {
        try {
            var maybeOnchainEventType = Enums.getIfPresent(OnChainEventType.class, payload.get("type").asText());
            if (maybeOnchainEventType.isEmpty() || maybeOnchainEventType.orElseThrow() != OnChainEventType.EVENT_REGISTRATION) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing type field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeTeam = Optional.ofNullable(payload.get("team").asText());
            if (maybeTeam.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing team field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeCreationSlot = Optional.ofNullable(payload.get("creationSlot")).map(JsonNode::asLong);
            if (maybeCreationSlot.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing creationSlot field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }

            var maybeVotingEventType = Enums.getIfPresent(VotingEventType.class, payload.get("votingEventType").asText());
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
                    .name(payload.get("name").asText())
                    .type(OnChainEventType.EVENT_REGISTRATION)
                    .team(maybeTeam.orElseThrow())
                    .creationSlot(maybeCreationSlot.orElseThrow())
                    .votingEventType(votingEventType);

            if (votingEventType == VotingEventType.STAKE_BASED) {
                var maybeStartEpoch = Optional.ofNullable(payload.get("startEpoch")).map(JsonNode::asInt);
                if (maybeStartEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndEpoch = Optional.ofNullable(payload.get("endEpoch")).map(JsonNode::asInt);;
                if (maybeEndEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                var maybeSnapshotEpoch = Optional.ofNullable(payload.get("snapshotEpoch")).map(JsonNode::asInt);
                if (maybeSnapshotEpoch.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing snapshotEpoch field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                eventRegistrationEnvelopeBuilder.startEpoch(maybeStartEpoch);
                eventRegistrationEnvelopeBuilder.endEpoch(maybeEndEpoch);
                eventRegistrationEnvelopeBuilder.snapshotEpoch(maybeSnapshotEpoch);
            }

            if (votingEventType == VotingEventType.USER_BASED) {
                var maybeStartSlot = Optional.ofNullable(payload.get("startSlot")).map(JsonNode::asLong);
                if (maybeStartSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing startSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }
                var maybeEndSlot = Optional.ofNullable(payload.get("endSlot")).map(JsonNode::asLong);
                if (maybeEndSlot.isEmpty()) {
                    return Either.left(
                            Problem.builder()
                                    .withTitle("INVALID_EVENT_REGISTRATION")
                                    .withDetail("Invalid event registration event, missing endSlot field.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                eventRegistrationEnvelopeBuilder.startSlot(maybeStartSlot);
                eventRegistrationEnvelopeBuilder.endSlot(maybeEndSlot);
            }

            var maybeOptions = Optional.ofNullable(payload.get("options"));
            if (maybeOptions.isEmpty()) {
                return Either.left(
                        Problem.builder()
                                .withTitle("INVALID_EVENT_REGISTRATION")
                                .withDetail("Invalid event registration event, missing options field.")
                                .withStatus(BAD_REQUEST)
                                .build());
            }
            var options = maybeOptions.orElseThrow();

            eventRegistrationEnvelopeBuilder.allowVoteChanging(fromInteger(options.get("allowVoteChanging").asInt()));
            eventRegistrationEnvelopeBuilder.categoryResultsWhileVoting(fromInteger(options.get("categoryResultsWhileVoting").asInt()));
            eventRegistrationEnvelopeBuilder.schemaVersion(payload.get("schemaVersion").asText());

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

    private static List<ProposalEnvelope> readProposalsEnvelope(JsonNode proposalsNode, boolean isGdprProtection) {
        var proposals = new ArrayList<ProposalEnvelope>();

        for (var it = proposalsNode.elements(); it.hasNext();) {
            var element = it.next();

            var proposalEnvelopeBuilder = ProposalEnvelope.builder()
                    .id(element.get("id").asText());

            if (!isGdprProtection) {
                proposalEnvelopeBuilder.name(element.get("name").asText());
            }

            proposals.add(proposalEnvelopeBuilder.build());
        }

        return proposals;
    }

}
