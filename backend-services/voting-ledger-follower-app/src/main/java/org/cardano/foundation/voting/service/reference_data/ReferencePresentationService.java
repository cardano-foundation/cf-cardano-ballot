package org.cardano.foundation.voting.service.reference_data;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ExpirationData;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.presentation.CategoryPresentation;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.cardano.foundation.voting.domain.presentation.ProposalPresentation;
import org.cardano.foundation.voting.service.epoch.CustomEpochService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferencePresentationService {

    private final ReferenceDataService referenceDataService;

    private final ExpirationService expirationService;

    private final CustomEpochService customEpochService;

    public Either<Problem, Optional<EventPresentation>> findEventReference(String name) {
        var maybeValidEventByName = referenceDataService.findValidEventByName(name);

        if (maybeValidEventByName.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var event = maybeValidEventByName.get();
        var categories = event.getCategories().stream().map(category -> {
                    var proposals = category.getProposals().stream().map(proposal -> ProposalPresentation.builder()
                                    .id(proposal.getId())
                                    .name(proposal.getName())
                                    .build())
                            .toList();

                    return CategoryPresentation.builder()
                            .id(category.getId())
                            .gdprProtection(category.isGdprProtection())
                            .proposals(proposals)
                            .build();
                }
        ).toList();

        var expirationDataE = expirationService.getExpirationData(event);
        if (expirationDataE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get expiration data.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
        var expirationData = expirationDataE.get();

        var eventBuilder = EventPresentation.builder()
                .id(event.getId())
                .organisers(event.getOrganisers())
                .votingEventType(event.getVotingEventType())
                .startEpoch(event.getStartEpoch())
                .endEpoch(event.getEndEpoch())
                .startSlot(event.getStartSlot())
                .endSlot(event.getEndSlot())
                .snapshotEpoch(event.getSnapshotEpoch())
                .categories(categories)
                .isNotStarted(expirationData.notStarted())
                .isActive(expirationData.active())
                .isFinished(expirationData.finished())
                .isProposalsReveal(expirationData.proposalsReveal())
                .isAllowVoteChanging(event.isAllowVoteChanging())
                .isHighLevelEventResultsWhileVoting(event.getHighLevelEpochResultsWhileVoting().orElse(false))
                .isHighLevelCategoryResultsWhileVoting(event.getHighLevelEpochResultsWhileVoting().orElse(false))
                .isCategoryResultsWhileVoting(event.getCategoryResultsWhileVoting().orElse(false));

        switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                eventBuilder.eventStartDate(customEpochService.getEpochStartTimeBasedOnEpochNo(event.getStartEpoch().orElseThrow()));
                eventBuilder.eventEndDate(customEpochService.getEpochEndTime(event.getEndEpoch().orElseThrow()));
                eventBuilder.snapshotTime(customEpochService.getEpochEndTime(event.getSnapshotEpoch().orElseThrow()));
                eventBuilder.proposalsRevealDate(customEpochService.getEpochEndTime(event.getProposalsRevealEpoch().orElseThrow()));
                eventBuilder.proposalsRevealEpoch(event.getProposalsRevealEpoch());
            }
            case USER_BASED -> {
                eventBuilder.eventStartDate(customEpochService.getEpochStartTimeBasedOnAbsoluteSlot(event.getStartSlot().orElseThrow()));
                eventBuilder.eventEndDate(customEpochService.getEpochEndTimeBasedOnAbsoluteSlot(event.getEndSlot().orElseThrow()));
                eventBuilder.proposalsRevealDate(customEpochService.getEpochEndTimeBasedOnAbsoluteSlot(event.getProposalsRevealSlot().orElseThrow()));
                eventBuilder.proposalsRevealSlot(event.getProposalsRevealSlot());
            }
        }

        return Either.right(Optional.of(eventBuilder.build()));
    }

    public Either<Problem, List<ExpirationData>> eventsSummaries() {
        List<Event> allValidEvents = referenceDataService.findAllValidEvents();

        return expirationService.getExpirationDataList(allValidEvents);
    }

}
