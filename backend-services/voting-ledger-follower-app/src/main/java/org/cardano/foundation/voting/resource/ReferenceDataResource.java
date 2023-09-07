package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.reference_data.ReferencePresentationService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/reference")
@Slf4j
@RequiredArgsConstructor
public class ReferenceDataResource {

    private final CacheManager cacheManager;

    private final ReferencePresentationService referencePresentationService;

    @RequestMapping(value = "/event/{name}", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.event", percentiles = { 0.3, 0.5, 0.95 } )
    @Cacheable("event_by_id")
    public ResponseEntity<?> getEventByName(@PathVariable String name) {
        return referencePresentationService.findEventReference(name)
                .map(eventReference -> ResponseEntity.ok().body(eventReference)
                ).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/event", method = GET, produces = "application/json")
    @Timed(value = "resource.reference.events", percentiles = { 0.3, 0.5, 0.95 } )
    @Cacheable("events")
    public ResponseEntity<?> events() {
        return ResponseEntity.ok(referencePresentationService.eventsData());
    }

    @Scheduled(fixedRateString = "PT1M")
    public void eventsClearCache() {
        log.info("event_by_id and events cache evictions...");

        cacheManager.getCache("event_by_id").clear();
        cacheManager.getCache("events").clear();
    }

}
