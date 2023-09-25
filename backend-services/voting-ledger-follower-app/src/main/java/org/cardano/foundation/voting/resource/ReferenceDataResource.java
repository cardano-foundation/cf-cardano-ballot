package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferencePresentationService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.problem.Status.NOT_FOUND;

@RestController
@RequestMapping("/api/reference")
@Slf4j
@RequiredArgsConstructor
public class ReferenceDataResource {

    private final ReferencePresentationService referencePresentationService;

    @RequestMapping(value = "/event/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", histogram = true)
    public ResponseEntity<?> getEventByName(@PathVariable("eventId") String eventId) {
        var cacheControl = CacheControl.maxAge(15, SECONDS)
                .noTransform()
                .mustRevalidate();

        return referencePresentationService.findEventReference(eventId)
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        maybeEventPresentation -> {
                            if (maybeEventPresentation.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("EVENT_NOT_FOUND")
                                        .withDetail("Event with id: " + eventId + " not found!")
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .cacheControl(cacheControl)
                                        .body(problem);
                            }

                            var eventPresentation = maybeEventPresentation.orElseThrow();

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(eventPresentation);
                        }
                );
    }

    @RequestMapping(value = "/event", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.events", histogram = true)
    public ResponseEntity<?> events() {
        var cacheControl = CacheControl.maxAge(15, SECONDS)
                .noTransform()
                .mustRevalidate();

        return referencePresentationService.eventsSummaries()
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        eventSummaries -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(eventSummaries);
                        }
                );
    }

}
