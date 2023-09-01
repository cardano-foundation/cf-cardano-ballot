package org.cardano.foundation.voting.service.reference_data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.presentation.CategoryPresentation;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.cardano.foundation.voting.domain.presentation.ProposalPresentation;
import org.cardano.foundation.voting.service.epoch.CustomEpochService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferencePresentationService {

    private final ReferenceDataService referenceDataService;

    private final ExpirationService expirationService;

    private final CustomEpochService customEpochService;

    public Optional<EventPresentation> findEventReference(String name) {
        return referenceDataService.findValidEventByName(name).map(event -> {
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

            var eventBuilder = EventPresentation.builder()
                    .id(event.getId())
                    .team(event.getTeam())
                    .votingEventType(event.getVotingEventType())
                    .startEpoch(event.getStartEpoch())
                    .endEpoch(event.getEndEpoch())
                    .startSlot(event.getStartSlot())
                    .endSlot(event.getEndSlot())
                    .snapshotEpoch(event.getSnapshotEpoch())
                    .categories(categories)
                    .isNotStarted(expirationService.isEventNotStarted(event))
                    .isActive(expirationService.isEventActive(event))
                    .isFinished(expirationService.isEventFinished(event))
                    .isAllowVoteChanging(event.isAllowVoteChanging())
                    .isHighLevelResultsWhileVoting(event.isHighLevelResultsWhileVoting())
                    .isCategoryResultsWhileVoting(event.isCategoryResultsWhileVoting());

            switch (event.getVotingEventType()) {
                case STAKE_BASED, BALANCE_BASED -> {
                    eventBuilder.eventStart(customEpochService.getEpochStartTimeBasedOnEpochNo(event.getStartEpoch().orElseThrow()));
                    eventBuilder.eventEnd(customEpochService.getEpochEndTime(event.getEndEpoch().orElseThrow()));
                    eventBuilder.snapshotTime(customEpochService.getEpochEndTime(event.getSnapshotEpoch().orElseThrow()));
                }
                case USER_BASED -> {
                    eventBuilder.eventStart(customEpochService.getEpochStartTimeBasedOnAbsoluteSlot(event.getStartSlot().orElseThrow()));
                    eventBuilder.eventEnd(customEpochService.getEpochEndTimeBasedOnAbsoluteSlot(event.getEndSlot().orElseThrow()));
                }
            }

            return eventBuilder.build();
        });
    }

    public List<Map<String, Object>> eventsData() {
        return referenceDataService.findAllValidEvents().stream().map(event -> Map.<String, Object>of(
                "id", event.getId(),
                "notStarted", expirationService.isEventNotStarted(event),
                "finished", expirationService.isEventFinished(event),
                "active", expirationService.isEventActive(event)
        )).toList();
    }

}
