package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api/discord/user-verification")
@Slf4j
public class DiscordUserVerificationResource {

    @Autowired
    private DiscordUserVerificationService discordUserVerificationService;

    @Value("${discord.bot.eventId.binding}")
    private String discordBotEventIdBinding;

    @RequestMapping(value = "/is-verified/{discordIdHash}", method = GET, produces = "application/json")
    @Timed(value = "resource.discord.isVerified", histogram = true)
    public ResponseEntity<?> isDiscordUserVerified(@PathVariable("discordIdHash") String discordIdHash) {
        log.info("Received isDiscordUserVerified request discordIdHash: {}", discordIdHash);

        return discordUserVerificationService.isVerifiedBasedOnDiscordIdHash(discordBotEventIdBinding, discordIdHash)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity.ok().body(userVerification);
                        }
                );
    }

    @RequestMapping(value = "/start-verification", method = { POST, PUT }, produces = "application/json")
    @Timed(value = "resource.discord.startVerification", histogram = true)
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

    @RequestMapping(value = "/check-verification", method = { POST , PUT }, produces = "application/json")
    @Timed(value = "resource.discord.checkVerification", histogram = true)
    public ResponseEntity<?> checkVerification(@RequestBody @Valid DiscordCheckVerificationRequest checkVerificationRequest) {
        log.info("Received discord checkVerification request: {}", checkVerificationRequest);

        return discordUserVerificationService.checkVerification(discordBotEventIdBinding, checkVerificationRequest)
                .fold(problem -> {
                            return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                        },
                        userVerification -> {
                            return ResponseEntity.ok().body(userVerification);
                        }
                );
    }

}
