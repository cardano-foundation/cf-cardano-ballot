package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/merkle-root-hash")
@Slf4j
public class MerkleRootHashResource {

    private final MerkleRootHashService merkleRootHashService;

    @RequestMapping(value = "/{eventId}/{merkleRootHashHex}", method = GET, produces = "application/json")
    @Timed(value = "resource.merkle_root_hash.find", histogram = true)
    public ResponseEntity<?> isValidMerkleRootHash(@PathVariable("eventId") String eventId,
                                                   @PathVariable("merkleRootHashHex") String merkleRootHashHex) {
        return merkleRootHashService.isPresent(eventId, merkleRootHashHex)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        isMerkleRootPresentResult -> {
                            return ResponseEntity.ok().body(isMerkleRootPresentResult);
                        }
                );
    }

}
