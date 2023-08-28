package org.cardano.foundation.voting.jobs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.repository.UserVerificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneCleanupJob {

    private final UserVerificationRepository userVerificationRepository;

    private final Clock clock;

    @Scheduled(fixedDelayString = "${phone.cleanup.job.interval:1H}")
    public void cleanup() {
        log.info("Running phone cleanup job...");

        userVerificationRepository.findAllPending().forEach(userVerification -> {
            log.info("Deleting pending user verification: {}", userVerification);

            var now = LocalDateTime.now(clock);

            // we delete all pending user verifications that are older than 1 day
            if (now.isAfter(userVerification.getUpdatedAt().plusDays(1))) {
                log.info("Deleting expired pending user verification: {}", userVerification);

                userVerificationRepository.delete(userVerification);
            }
        });
    }

}
