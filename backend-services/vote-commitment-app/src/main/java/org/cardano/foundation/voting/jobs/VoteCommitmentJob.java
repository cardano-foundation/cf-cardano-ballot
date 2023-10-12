package org.cardano.foundation.voting.jobs;

import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.merkle_tree.VoteCommitmentService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "vote.commitment", value = "enabled", havingValue = "true")
public class VoteCommitmentJob implements Runnable {

    private final VoteCommitmentService voteCommitmentService;

    @PostConstruct
    public void init() {
        run();
    }

    @Override
    //@Scheduled(cron = "${vote.commitment.cron.expression}")
    @Timed(value = "vote.commitment.cron.job", histogram = true)
    public void run() {
        log.info("Starting VoteCommitmentJob...");

        var startStop = new StopWatch();
        startStop.start();

        voteCommitmentService.processVotesForAllEvents();

        startStop.stop();

        log.info("VoteCommitmentJob completed, running time:{} secs", startStop.getTotalTimeSeconds());
    }

}

// 1 per day

// monitoring