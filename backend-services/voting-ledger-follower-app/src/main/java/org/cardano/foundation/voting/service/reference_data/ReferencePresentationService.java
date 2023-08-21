package org.cardano.foundation.voting.service.reference_data;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.presentation.CategoryPresentation;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.cardano.foundation.voting.domain.presentation.ProposalPresentation;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.service.epoch.CustomEpochService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.i18n.LocalisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ReferencePresentationService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private LocalisationService localisationService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private CustomEpochService customEpochService;

    public Optional<EventPresentation> findEventReference(String name, Locale locale) {
        return referenceDataService.findValidEventByName(name).map(event -> {
            var categories = event.getCategories().stream().map(category -> {
                        var proposals = category.getProposals().stream().map(proposal -> ProposalPresentation.builder()
                                        .id(proposal.getId())
                                        .name(proposal.getName())
                                        .presentationName(localisationService.translate(name, proposal.getName(), locale))
                                        .build())
                                .toList();

                        return CategoryPresentation.builder()
                                .id(category.getId())
                                .gdprProtection(category.isGdprProtection())
                                .presentationName(localisationService.translate(name, category.getId(), locale))
                                .proposals(proposals)
                                .build();
                    }
            ).toList();

            var eventBuilder = EventPresentation.builder()
                    .id(event.getId())
                    .presentationName(localisationService.translate(name, event.getId(), locale))
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
                "name", event.getId(),
                "notStarted", expirationService.isEventNotStarted(event),
                "finished", expirationService.isEventFinished(event),
                "active", expirationService.isEventActive(event)
        )).toList();
    }

}
