package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/merkle-root-hash")
@Slf4j
public class MerkleRootHashResource {

    private final CacheManager cacheManager;

    private final MerkleRootHashService merkleRootHashService;

    @RequestMapping(value = "/{eventId}/{merkleRootHashHex}", method = GET, produces = "application/json")
    @Timed(value = "resource.merkle_root_hash.find", percentiles = {0.3, 0.5, 0.95})
    @Cacheable("merkle_root_hashes")
    public ResponseEntity<?> isValidMerkleRootHash(@PathVariable("eventId") String eventId,
                                                   @PathVariable("merkleRootHashHex") String merkleRootHashHex) {
        return merkleRootHashService.isPresent(eventId, merkleRootHashHex)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        isMerkleRootPresentResult -> ResponseEntity.ok().body(isMerkleRootPresentResult)
                );
    }

    @Scheduled(fixedRateString = "PT1H")
    public void clearCache() {
        log.info("merkle_root_hashes cache eviction...");

        cacheManager.getCache("merkle_root_hashes").clear();
    }

}
