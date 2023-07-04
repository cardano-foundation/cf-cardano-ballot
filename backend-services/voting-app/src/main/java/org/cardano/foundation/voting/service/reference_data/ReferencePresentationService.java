package org.cardano.foundation.voting.service.reference_data;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.reference.CategoryReference;
import org.cardano.foundation.voting.domain.reference.EventReference;
import org.cardano.foundation.voting.domain.reference.ProposalReference;
import org.cardano.foundation.voting.repository.EventRepository;
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

    public Optional<EventReference> findEventReference(String name, Locale locale) {
        return referenceDataService.findValidEventByName(name).map(event -> {
            var categories = event.getCategories().stream().map(category -> {
                        var proposals = category.getProposals().stream().map(proposal -> ProposalReference.builder()
                                        .id(proposal.getId())
                                        .name(proposal.getName())
                                        .presentationName(localisationService.translate(name, proposal.getName(), locale))
                                        .build())
                                .toList();

                        return CategoryReference.builder()
                                .id(category.getId())
                                .gdprProtection(category.isGdprProtection())
                                .presentationName(localisationService.translate(name, category.getId(), locale))
                                .proposals(proposals)
                                .build();
                    }
            ).toList();

            return EventReference.builder()
                    .id(event.getId())
                    .presentationName(localisationService.translate(name, event.getId(), locale))
                    .team(event.getTeam())
                    .votingEventType(event.getVotingEventType())
                    .startEpoch(Optional.ofNullable(event.getStartEpoch()))
                    .endEpoch(Optional.ofNullable(event.getEndEpoch()))
                    .startSlot(Optional.ofNullable(event.getStartSlot()))
                    .endSlot(Optional.ofNullable(event.getEndSlot()))
                    .snapshotEpoch(Optional.ofNullable(event.getSnapshotEpoch()))
                    .categories(categories)
                    .isActive(expirationService.isEventActive(event))
                    .build();
        });
    }

    public List<Map<String, Object>> eventsData() {
        return referenceDataService.findAllValidEvents().stream().map(e -> {
            return Map.<String, Object>of(
                    "name", e.getId(),
                    "active", expirationService.isEventActive(e)
            );
        }).toList();
    }

}
