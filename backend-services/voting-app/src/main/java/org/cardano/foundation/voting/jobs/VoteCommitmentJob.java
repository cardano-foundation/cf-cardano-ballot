package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.merkle_tree.VoteCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableAsync
public class VoteCommitmentJob implements Runnable {

    @Autowired
    private VoteCommitmentService voteCommitmentService;

    @Value("${vote.commitment.enabled}")
    private boolean isVoteCommitmentEnabled;

    @Override
    @Scheduled(cron = "${vote.commitment.cron.expression}")
    @Async
    public void run() {
        if (!isVoteCommitmentEnabled) {
            log.info("L1 votes commitment disabled on this instance.");
            return;
        }

        log.info("Starting VoteCommitmentJob...");

        voteCommitmentService.processVotesForAllEvents();

        log.info("Finished processing events and all votes...");
    }

}
