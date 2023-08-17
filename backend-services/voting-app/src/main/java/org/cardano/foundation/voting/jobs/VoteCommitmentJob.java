package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.merkle_tree.VoteCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class VoteCommitmentJob implements Runnable {

    @Autowired
    private VoteCommitmentService voteCommitmentService;

    @Value("${vote.commitment.enabled}")
    private boolean isVoteCommitmentEnabled;

    @Override
    @Scheduled(cron = "${vote.commitment.cron.expression}")
    public void run() {
        if (!isVoteCommitmentEnabled) {
            log.info("L1 votes commitment disabled on this instance.");
            return;
        }

        log.info("Starting VoteCommitmentJob...");
        var startStop = new StopWatch();
        startStop.start();
        voteCommitmentService.processVotesForAllEvents();
        startStop.stop();

        log.info("VoteCommitmentJob completed, running time:{} secs", startStop.getTotalTimeSeconds());
    }

}
