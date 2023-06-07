package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/reference")
@Slf4j
public class ReferenceDataResource {

    @Autowired
    private ReferenceDataService referenceDataService;

    @RequestMapping(value = "/event", method = POST, produces = "application/json")
    @Timed(value = "resource.reference.event", percentiles = { 0.3, 0.5, 0.95 } )
    public ResponseEntity<?> getEventByName(String name) {
        var maybeEvent = referenceDataService.findEventByName(name);
        if (maybeEvent.isEmpty()) {
            // TODO make a better error e.g. using Zalando Problem
            // https://github.com/zalando/problem
            return ResponseEntity.notFound().build();
        }
        var event = maybeEvent.get();

        return ResponseEntity.ok(event);
    }

}
