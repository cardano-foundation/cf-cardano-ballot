package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1SubmissionData;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
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
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Autowired
    private Clock clock;

    @Value("${transaction.submission.timeout.minutes:15}")
    private int timeoutInMinutes;

    @Value("${transaction.submission.sleep.seconds:5}")
    private int sleepTimeInSeconds;

    @Override
    public String submitTransaction(byte[] txData) {
        return transactionSubmissionService.submitTransaction(txData);
    }

    @Override
    public L1SubmissionData submitTransactionWithConfirmation(byte[] txData) throws TimeoutException {
        var txHash = TransactionUtil.getTxHash(txData);

        var start = LocalDateTime.now(clock);

        var future = start.plusMinutes(timeoutInMinutes);

        while (LocalDateTime.now(clock).isBefore(future)) {
            var transactionDetails = blockchainDataTransactionDetailsService.getTransactionDetails(txHash);
            if (transactionDetails.isPresent()) {
                return new L1SubmissionData(txHash, transactionDetails.get().getAbsoluteSlot());
            }
            try {
                Thread.sleep(sleepTimeInSeconds * 1000L);
            } catch (InterruptedException e) {
                log.warn("Interrupted while sleeping...", e);
            }
        }

        throw new TimeoutException("Transaction not confirmed within timeout");
    }

}
