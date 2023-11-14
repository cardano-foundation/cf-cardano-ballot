package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.sms.SMSCheckVerificationRequest;
import org.cardano.foundation.voting.domain.sms.SMSStartVerificationRequest;
import org.cardano.foundation.voting.domain.sms.SMSStartVerificationResponse;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/sms/user-verification")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "SMS User Verification", description = "Operations related to SMS user verification")
public class SMSUserVerificationResource {

    private final SMSUserVerificationService smsUserVerificationService;

    @RequestMapping(value = "/start-verification", method = { PUT, POST }, produces = "application/json")
    @Timed(value = "resource.sms.startVerification", histogram = true)
    @Operation(
            summary = "Initiate the verification process for a user via SMS",
            description = "Starts the verification process based on the provided SMSStartVerificationRequest.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully initiated the verification process.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SMSStartVerificationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to an invalid startVerificationRequest.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> startVerification(@RequestBody @Valid SMSStartVerificationRequest startVerificationRequest) {
        log.info("Received SMS startVerification request: {}", startVerificationRequest);

        return smsUserVerificationService.startVerification(startVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity
                                    .ok()
                                    .body(userVerification);
                        }
                );
    }

    @RequestMapping(value = "/check-verification", method = { POST }, produces = "application/json")
    @Timed(value = "resource.sms.checkVerification", histogram = true)
    @Operation(
            summary = "Check the verification status for a user via SMS",
            description = "Verifies the user's status based on the provided SMSCheckVerificationRequest.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked the verification status.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = IsVerifiedResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to an invalid SMS code in the check verification request.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> checkVerification(@RequestBody @Valid SMSCheckVerificationRequest checkVerificationRequest) {
        log.info("Received SMS checkVerification request: {}", checkVerificationRequest);

        return smsUserVerificationService.checkVerification(checkVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity
                                    .ok()
                                    .body(userVerification);
                        }
                );
    }

}
