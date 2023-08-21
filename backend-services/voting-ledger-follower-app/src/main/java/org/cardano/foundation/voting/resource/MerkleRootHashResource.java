package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/merkle_root_hash")
@Slf4j
public class MerkleRootHashResource {

    private final MerkleRootHashService merkleRootHashService;

    private final CardanoNetwork network;

    @RequestMapping(value = "/{event}/{merkleRootHashHex}", method = GET, produces = "application/json")
    @Timed(value = "resource.merkle_root_hash.find", percentiles = {0.3, 0.5, 0.95})
    public ResponseEntity<?> isValidMerkleRootHash(@PathVariable String event, @PathVariable String merkleRootHashHex) {
        return merkleRootHashService.isPresent(event, merkleRootHashHex)
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        isPresent -> ResponseEntity.ok()
                                .body(Map.of("isPresent", isPresent, "network", network.name()))
                );
    }

}
