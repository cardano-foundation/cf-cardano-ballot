package org.cardano.foundation.voting.jobs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.merkle_tree.VoteCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VoteCommitmentJob {

    @Autowired
    private VoteCommitmentService voteCommitmentService;

    @PostConstruct
    @Async
    public void onStart() {
        log.info("On startup...");
        processVotesForAllEvents();
        log.info("On startup...done.");
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void processVotesForAllEvents() {
        log.info("Starting VoteCommitmentJob...");
        log.info("Processing events...");

        voteCommitmentService.processVotesForAllEvents();

        log.info("Finished processing events and all votes...");
    }

}
