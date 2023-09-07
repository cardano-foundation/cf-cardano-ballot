package org.cardano.foundation.voting.service.transaction_submit;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.L1SubmissionData;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class DefaultTransactionSubmissionService implements TransactionSubmissionService {

    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private Clock clock;

    @Value("${transaction.submission.timeout.minutes:5}")
    private int timeoutInMinutes;

    @Value("${transaction.submission.sleep.seconds:5}")
    private int sleepTimeInSeconds;

    @Override
    public String submitTransaction(byte[] txData) {
        return transactionSubmissionService.submitTransaction(txData);
    }

    @Override
    public L1SubmissionData submitTransactionWithConfirmation(byte[] txData) throws TimeoutException, InterruptedException {
        var txHash = submitTransaction(txData);

        var start = LocalDateTime.now(clock);

        var future = start.plusMinutes(timeoutInMinutes);

        while (LocalDateTime.now(clock).isBefore(future)) {
            var transactionDetailsE = chainFollowerClient.getTransactionDetails(txHash);
            if (transactionDetailsE.isEmpty() || transactionDetailsE.get().isEmpty()) {
                log.warn("Transaction not found in chain follower yet. Sleeping for {} seconds... until deadline:{}", sleepTimeInSeconds, future);
                Thread.sleep(sleepTimeInSeconds * 1000L);
                continue;
            }

            var transactionDetails = transactionDetailsE.get().orElseThrow();

            log.info("Transaction found, details:{}", transactionDetails);

            return new L1SubmissionData(txHash, transactionDetails.absoluteSlot());
        }

        throw new TimeoutException("Transaction not confirmed within timeout!");
    }

}
