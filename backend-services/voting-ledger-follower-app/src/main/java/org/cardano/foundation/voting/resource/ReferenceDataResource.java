package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.cardano.foundation.voting.service.reference_data.ReferencePresentationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/reference")
@Slf4j
@RequiredArgsConstructor
public class ReferenceDataResource {

    private final ReferencePresentationService referencePresentationService;

    @RequestMapping(value = "/event/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", histogram = true)
    public ResponseEntity<?> getEventByName(@PathVariable("eventId") String eventId) {
        return referencePresentationService.findEventReference(eventId)
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        maybeEventPresentation -> {
                            if (maybeEventPresentation.isEmpty()) {
                                return ResponseEntity.notFound().build();
                            }

                            EventPresentation eventPresentation = maybeEventPresentation.orElseThrow();

                            return ResponseEntity.ok().body(eventPresentation);
                        }
                );
    }

    @RequestMapping(value = "/event", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.events", histogram = true)
    public ResponseEntity<?> events() {
        return ResponseEntity.ok(referencePresentationService.eventsExpirationData());
    }

}
