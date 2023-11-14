package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationResponse;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.zalando.problem.Status.BAD_REQUEST;

@RestController
@RequestMapping("/api/discord/user-verification")
@Slf4j
@Tag(name = "Discord User Verification", description = "Operations related to Discord user verification")
public class DiscordUserVerificationResource {

    @Autowired
    private DiscordUserVerificationService discordUserVerificationService;

    @Value("${discord.bot.eventId.binding}")
    private String discordBotEventIdBinding;

    @RequestMapping(value = "/is-verified/{discordIdHash}", method = GET, produces = "application/json")
    @Timed(value = "resource.discord.isVerified", histogram = true)
    @Operation(summary = "Check if a Discord user is verified",
            description = "Determines if a user, based on their Discord ID hash, is verified.",
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
                            description = "Bad request, possibly due to an invalid discordIdHash.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> isDiscordUserVerified(@PathVariable("discordIdHash") String discordIdHash) {
        log.info("Received isDiscordUserVerified request discordIdHash: {}", discordIdHash);

        return discordUserVerificationService.isVerifiedBasedOnDiscordIdHash(discordBotEventIdBinding, discordIdHash)
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

    @RequestMapping(value = "/start-verification", method = { POST, PUT }, produces = "application/json")
    @Timed(value = "resource.discord.startVerification", histogram = true)
    @Operation(summary = "Start the verification process for a Discord user",
            description = "Initiates the verification process based on the provided DiscordStartVerificationRequest.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully started the verification process.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiscordStartVerificationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to invalid data in the startVerificationRequest.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> startVerification(@RequestBody @Valid DiscordStartVerificationRequest startVerificationRequest) {
        log.info("Received discord startVerification request: {}", startVerificationRequest);

        return discordUserVerificationService.startVerification(discordBotEventIdBinding, startVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity.ok().body(userVerification);
                        }
                );
    }

    @RequestMapping(value = "/check-verification", method = { POST }, produces = "application/json")
    @Timed(value = "resource.discord.checkVerification", histogram = true)
    @Operation(summary = "Check the verification status for a Discord user",
            description = "Checks the verification status based on the provided DiscordCheckVerificationRequest.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the verification status.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = IsVerifiedResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, possibly due to an invalid checkVerificationRequest or event ID mismatch.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> checkVerification(@RequestBody @Valid DiscordCheckVerificationRequest checkVerificationRequest) {
        log.info("Received discord checkVerification request: {}", checkVerificationRequest);

        if (!checkVerificationRequest.getEventId().equals(discordBotEventIdBinding)) {
            return ResponseEntity.badRequest().
                    body(Problem.builder().withTitle("EVENT_ID_AND_DISCORD_ID_BOT_MISMATCH")
                            .withDetail("Event id and discord id bot mismatch!")
                            .withStatus(BAD_REQUEST)
                            .build());
        }

        return discordUserVerificationService.checkVerification(checkVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity.ok().body(userVerification);
                        }
                );
    }

}
