package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.cardano.foundation.voting.service.TransactionSubmissionService;
import org.cardano.foundation.voting.service.VoteCommitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class VoteCommitmentJob {

    @Autowired
    private VoteCommitmentService voteCommitmentService;

    @Autowired
    private TransactionSubmissionService transactionSubmissionService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void postRootHash() {
        log.info("Running posting root hash job...");

        referenceDataService.findAllEvents().forEach(event -> {
            var rootHash = voteCommitmentService.storeLatestRootHash(event);

            transactionSubmissionService.submitTransaction(event, rootHash.getRootHash());
        });

    }

}
