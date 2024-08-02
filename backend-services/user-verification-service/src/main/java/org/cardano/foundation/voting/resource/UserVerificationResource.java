package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.common.UserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/user-verification")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User Verification", description = "Operations related to user verification")
public class UserVerificationResource {

    private final UserVerificationService userVerificationService;

    @RequestMapping(value = "/verified/{eventId}/{walletId}", method = GET, produces = "application/json")
    @Timed(value = "resource.isVerified", histogram = true)
    @Operation(
            summary = "Check the verification status for a user based on event ID and stake address",
            description = "Determines if a user, based on their event ID and stake address, is verified.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved verification status.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = IsVerifiedResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to an invalid eventId or walletId.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> isVerified(@PathVariable("eventId") String eventId,
                                        @PathVariable("walletId") String walletId) {
        var isVerifiedRequest = new IsVerifiedRequest(eventId, walletId);

        return userVerificationService.isVerified(isVerifiedRequest)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        isVerifiedResponse -> {
                            return ResponseEntity
                                    .ok()
                                    .body(isVerifiedResponse);
                        }
                );
    }

}
