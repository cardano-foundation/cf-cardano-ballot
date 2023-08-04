package org.cardano.foundation.voting.service.transaction_submit;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.L1SubmissionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private TransactionSubmissionService transactionSubmissionService;

    @Autowired
    private L1TransactionCreator l1TransactionCreator;

    public Either<Problem, L1SubmissionData> submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        var txDataE = l1TransactionCreator.submitMerkleCommitments(l1MerkleCommitments);

        if (txDataE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("TRANSACTION_SUBMISSION__FAILED")
                    .withDetail("Reason:" + txDataE.getLeft().getDetail())
                    .build());
        }
        var txData = txDataE.get();

        try {
            return Either.right(transactionSubmissionService.submitTransactionWithConfirmation(txData));
        } catch (TimeoutException e) {
            return Either.left(Problem.builder()
                    .withTitle("TRANSACTION_SUBMISSION_TIMEOUT")
                    .withDetail("Transaction submitted but timed out waiting for confirmation...")
                    .build());
        }
    }

}
