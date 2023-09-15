package org.cardano.foundation.voting.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.service.discord.DiscordUserVerificationService;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanupAllForFinishedEventsJob implements Runnable {

    private final ChainFollowerClient chainFollowerClient;

    private final SMSUserVerificationService smsUserVerificationService;

    private final DiscordUserVerificationService discordUserVerificationService;

    @Scheduled(cron = "${finished.verifications.cleanup.job.cron}")
    public void run() {
        log.info("Cleaning up verified phones for event...");

        var allEventsE = chainFollowerClient.findAllEvents();

        if (allEventsE.isEmpty()) {
            log.warn("No events found in ledger follower, skipping cleanup job.");
            return;
        }

        var allEvents = allEventsE.get();

        allEvents.forEach(eventSummary -> {
            if (eventSummary.finished()) {

                log.info("Event:{} is finished, removing all sms verifications...", eventSummary.id());
                smsUserVerificationService.findAllForEvent(eventSummary.id()).forEach(userVerification -> {
                    log.info("Removing historical user sms verification... since eventId:{} is finished.", eventSummary.id());
                    smsUserVerificationService.removeUserVerification(userVerification);
                });

                log.info("Event:{} is finished, removing all discord verifications...", eventSummary.id());
                discordUserVerificationService.findAllForEvent(eventSummary.id()).forEach(userVerification -> {
                    log.info("Removing historical user discord verification... since eventId:{} is finished.", eventSummary.id());
                    discordUserVerificationService.removeUserVerification(userVerification);
                });

            }
        });
    }

}
