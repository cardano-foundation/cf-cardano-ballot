package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.merkle_tree.VoteCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VoteProofsJobs {

    @Autowired
    private VoteCommitmentService voteCommitmentService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void processVotesForAllEvents() {
        log.info("Starting VoteCommitmentJob...");
        log.info("Processing events...");

        voteCommitmentService.storeAllVoteProofs();

        log.info("Finished processing events and all votes...");
    }

}
