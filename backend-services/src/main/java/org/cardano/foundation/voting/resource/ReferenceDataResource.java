package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
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
        return referenceDataService.findEventReference(name)
                .map(eventReference -> ResponseEntity.ok().body(eventReference)
                ).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
