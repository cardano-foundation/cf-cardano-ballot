package org.cardano.foundation.voting.resource;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminResource {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private Executor executor;

    // TODO authorisation for admin, e.g. via HTTP BASIC AUTH or JWT

    @RequestMapping(value = "/full-metadata-scan", method = POST, produces = "application/json")
    public ResponseEntity<?> processAllMetadataEvents() {
        log.info("Received full metadata scan signal...");

        executor.execute(() -> metadataService.processAllMetadataEvents());

        log.info("Full metadata scan signal completed.");

        return ResponseEntity.ok().build();
    }

}
