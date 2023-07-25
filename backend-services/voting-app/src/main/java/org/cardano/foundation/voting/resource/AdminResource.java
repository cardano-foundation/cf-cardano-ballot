package org.cardano.foundation.voting.resource;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping(value = "/full-metadata-scan", method = POST, produces = "application/json")
    public ResponseEntity<?> processAllMetadataEvents(@RequestBody @Valid SignedWeb3Request fullMetadataScanRequest) {
        log.info("Received full metadata scan signal...");

        executor.execute(() -> {
            var result = metadataService.processAllMetadataEvents(fullMetadataScanRequest);
            if (result.isLeft()) {
                var problem = result.getLeft();
                log.error("Error processing full metadata scan: {}", problem);
            }
            if (result.isRight()) {
                log.info("Successfully processed full metadata.");
            }
        });

        log.info("Full metadata scan signal completed.");

        return ResponseEntity.ok().build();
    }

}
