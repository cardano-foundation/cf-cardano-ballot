package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PendingVerificationCleanupJob implements Runnable {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private SMSUserVerificationService smsUserVerificationService;

    @Autowired
    private DiscordUserVerificationService discordUserVerificationService;

    @Autowired
    private Clock clock;

    @Value("${pending.verification.phone.expiration.time.hours}")
    private int pendingVerificationPhoneExpirationTimeHours;

    @Scheduled(cron = "${pending.verification.phone.cleanup.job.cron}")
    public void run() {
        log.info("Running pending phone cleanup job...");

        var allEventsE = chainFollowerClient.findAllEvents();

        if (allEventsE.isEmpty()) {
            log.warn("No events found in ledger follower, skipping pending phone cleanup job");
            return;
        }

        var allEvents = allEventsE.get();

        allEvents.forEach(eventSummary -> {
            var id = eventSummary.id();

            log.info("Cleaning up pending phone verifications for event: {}", id);
            smsUserVerificationService.findAllPending(id).forEach(userVerification -> {
                var now = LocalDateTime.now(clock);

                boolean expiredVerification = now.isAfter(userVerification.getCreatedAt()
                        .plusHours(pendingVerificationPhoneExpirationTimeHours));

                if (expiredVerification) {
                    log.info("Deleting expired pending user verification: {}", userVerification);

                    smsUserVerificationService.removeUserVerification(userVerification);
                }
            });

            log.info("Cleaning up pending discord verifications for event: {}", id);
            discordUserVerificationService.findAllPending(id).forEach(userVerification -> {
                var now = LocalDateTime.now(clock);

                boolean expiredVerification = now.isAfter(userVerification.getCreatedAt()
                        .plusHours(pendingVerificationPhoneExpirationTimeHours));

                if (expiredVerification) {
                    log.info("Deleting expired pending user verification: {}", userVerification);
                    discordUserVerificationService.removeUserVerification(userVerification);
                }
            });

        });
    }

}
