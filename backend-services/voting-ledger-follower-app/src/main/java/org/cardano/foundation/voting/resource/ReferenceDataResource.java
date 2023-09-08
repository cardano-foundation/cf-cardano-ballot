package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferencePresentationService;
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
    private ReferencePresentationService referencePresentationService;

    @RequestMapping(value = "/event/{name}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", histogram = true)
    public ResponseEntity<?> getEventByName(@PathVariable String name) {
        return referencePresentationService.findEventReference(name)
                .map(eventReference -> ResponseEntity.ok().body(eventReference)
                ).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/event", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.events", histogram = true)
    public ResponseEntity<?> events() {
        return ResponseEntity.ok(referencePresentationService.eventsData());
    }

}
