package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.DiscordStartVerificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/discord/user-verification")
@Slf4j
@RequiredArgsConstructor
public class DiscordUserVerificationResource {

    @RequestMapping(value = "/is-verified/{discordIdHash}", method = GET, produces = "application/json")
    @Timed(value = "resource.discord.isVerified", histogram = true)
    public ResponseEntity<?> isDiscordUserVerified(@PathVariable("hashedDiscordId") String hashedDiscordId) {
        log.info("Received isDiscordUserVerified hashedDiscordId: {}", hashedDiscordId);

        // TODO

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/start-verification", method = POST, produces = "application/json")
    @Timed(value = "resource.discord.startVerification", histogram = true)
    public ResponseEntity<?> startVerification(@RequestBody @Valid DiscordStartVerificationRequest startVerificationRequest) {
        log.info("Received discord startVerification request: {}", startVerificationRequest);

        // TODO

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/check-verification", method = POST, produces = "application/json")
    @Timed(value = "resource.discord.checkVerification", histogram = true)
    public ResponseEntity<?> checkVerification(@RequestBody @Valid DiscordCheckVerificationRequest checkVerificationRequest) {
        log.info("Received discord checkVerification request: {}", checkVerificationRequest);

        // TODO

        return ResponseEntity.ok().build();
    }

}
