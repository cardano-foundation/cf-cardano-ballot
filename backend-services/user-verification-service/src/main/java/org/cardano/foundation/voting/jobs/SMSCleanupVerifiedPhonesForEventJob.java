package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.service.sms.SMSUserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SMSCleanupVerifiedPhonesForEventJob implements Runnable {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private SMSUserVerificationService smsUserVerificationService;

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
            smsUserVerificationService.findAllForEvent(eventSummary.id()).forEach(userVerification -> {
                if (eventSummary.finished()) {
                    log.info("Removing historical user verification... since eventId:{} is finished.", eventSummary.id());
                    smsUserVerificationService.removeUserVerification(userVerification);
                }
            });
        });
    }

}
