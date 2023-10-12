package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VoteVerificationRequest;
import org.cardano.foundation.voting.domain.VoteVerificationResult;
import org.cardano.foundation.voting.service.verify.VoteVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/verification")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Voting Verification", description = "The voting verification API")
public class VerificationResource {

    private final VoteVerificationService voteVerificationService;

    @RequestMapping(value = "/verify-vote", method = POST, produces = "application/json")
    @Timed(value = "resource.verifyVote", histogram = true)
    @Operation(
            summary = "Verify a vote",
            description = "Endpoint to verify the authenticity of a vote"
    )
    @ApiResponse(responseCode = "200", description = "Vote verified successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteVerificationResult.class)))
    @ApiResponse(responseCode = "400", description = "Bad request due to validation issues or malformed request",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Standard error response",
                            type = "object",
                            properties = {
                                    @StringToClassMapItem(key = "title", value = String.class),
                                    @StringToClassMapItem(key = "status", value = Integer.class),
                                    @StringToClassMapItem(key = "detail", value = String.class)
                            }
                    )
            ))
    @ApiResponse(responseCode = "500", description = "Server error while verifying vote")
    public ResponseEntity<?> verifyVote(@RequestBody @Valid
                                        @Parameter(description = "Vote verification request")
                                            VoteVerificationRequest voteVerificationRequest) {
        log.info("Received vote verification request: {}", voteVerificationRequest);

        return voteVerificationService.verifyVoteProof(voteVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        voteVerificationResult -> {
                            return ResponseEntity
                                    .ok()
                                    .body(voteVerificationResult);
                        }
                );
    }

}
