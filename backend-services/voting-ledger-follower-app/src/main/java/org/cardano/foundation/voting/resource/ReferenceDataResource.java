package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
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
@Tag(name = "ReferenceData", description = "The reference data API")
public class ReferenceDataResource {

    private final ReferencePresentationService referencePresentationService;

    @RequestMapping(value = "/event/{eventId}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", histogram = true)
    @Operation(summary = "Retrieve event details by event ID",
            description = "Fetches details of an event based on the provided event ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved event details",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EventPresentation.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getEventByName(
            @Parameter(description = "Event ID for which details are to be retrieved", required = true)
            @PathVariable("eventId") String eventId) {
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
    @Operation(summary = "Retrieve summaries of all events",
            description = "Fetches summaries of all available events in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved event summaries",
                            content = { @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventAdditionalInfo.class))) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
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
