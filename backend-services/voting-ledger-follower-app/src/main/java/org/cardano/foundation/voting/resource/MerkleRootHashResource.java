package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.vote.MerkleRootHashService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/merkle-root-hash")
@Slf4j
@Tag(name = "MerkleRootHashResource", description = "Merkle root hash operations API")
public class MerkleRootHashResource {

    private final MerkleRootHashService merkleRootHashService;

    @RequestMapping(value = "/{eventId}/{merkleRootHashHex}", method = GET, produces = "application/json")
    @Timed(value = "resource.merkle_root_hash.find", histogram = true)
    @Operation(summary = "Check validity of a Merkle root hash for a given event ID",
            description = "Validates the presence of a given Merkle root hash for a specified event ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully verified the presence of the Merkle root hash",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)) }),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request due to validation issues or Merkle root hash not found for the provided event ID",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> isValidMerkleRootHash(
            @Parameter(description = "Event ID associated with the Merkle root hash", required = true)
            @PathVariable("eventId") String eventId,
            @Parameter(description = "Merkle root hash to be validated", required = true)
            @PathVariable("merkleRootHashHex") String merkleRootHashHex) {
        var cacheControl = CacheControl.noCache()
                .noTransform()
                .mustRevalidate();

        return merkleRootHashService.isPresent(eventId, merkleRootHashHex)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        isMerkleRootPresentResult -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(isMerkleRootPresentResult);
                        }
                );
    }

}
