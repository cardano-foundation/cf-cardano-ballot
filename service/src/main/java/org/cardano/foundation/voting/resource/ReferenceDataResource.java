package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.reference.CategoryReference;
import org.cardano.foundation.voting.domain.reference.EventReference;
import org.cardano.foundation.voting.domain.reference.ProposalReference;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/reference")
@Slf4j
public class ReferenceDataResource {

    @Autowired
    private ReferenceDataService referenceDataService;

    @RequestMapping(value = "/event/{name}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", percentiles = { 0.3, 0.5, 0.95 } )
    public ResponseEntity<?> getEventByName(@PathVariable String name) {
        var maybeEvent = referenceDataService.findEventByName(name);
        if (maybeEvent.isEmpty()) {
            log.warn("Event with name {} not found", name);

            return ResponseEntity.notFound().build();
        }
        var event = maybeEvent.get();

        var categories = event.getCategories().stream().map(category -> {
            var proposals = category.getProposals().stream().map(proposal -> ProposalReference.builder()
                            .id(proposal.getId())
                            .name(proposal.getName())
                            .description(proposal.getDescription())
                            .presentationName(proposal.getPresentationName())
                            .build()).toList();

                    return CategoryReference.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .description(category.getDescription())
                            .presentationName(category.getPresentationName())
                            .proposals(proposals)
                            .build();
                }
        ).toList();

        return ResponseEntity.ok(EventReference.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .presentationName(event.getPresentationName())
                .team(event.getTeam())
                .startSlot(event.getStartSlot())
                .endSlot(event.getEndSlot())
                .snapshotEpoch(event.getSnapshotEpoch())
                .categories(categories)
            .build());
    }

}
