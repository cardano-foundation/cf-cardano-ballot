package org.cardano.foundation.voting.service.transaction_submit;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.CreateTallyResultCommand;
import org.cardano.foundation.voting.domain.HydraTallyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.cardano.foundation.voting.domain.TallyType.HYDRA;
import static org.cardano.foundation.voting.domain.VotingEventType.*;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private TransactionSubmissionService transactionSubmissionService;

    @Autowired
    private L1TransactionCreator l1TransactionCreator;

    @Value("${cardano.snapshot.bounds.check.enabled}")
    private boolean isSnapshotBoundsCheckEnabled = true;

    public String submitEvent(CreateEventCommand event) {
        checkEventCorrectness(event);

        byte[] txData = l1TransactionCreator.submitEvent(event);

        return transactionSubmissionService.submitTransaction(txData);
    }

    private void checkEventCorrectness(CreateEventCommand event) {
        if (List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            if (event.getStartEpoch().orElseThrow(() -> new RuntimeException("startEpoch required")) > event.getEndEpoch().orElseThrow(() -> new RuntimeException("endEpoch required!"))) {
                throw new IllegalArgumentException("Event start time must be before end time");
            }
            if (isSnapshotBoundsCheckEnabled) {
                if (event.getSnapshotEpoch().orElseThrow(() -> new RuntimeException("snapshotEpoch required")) >= event.getStartEpoch().orElseThrow(() -> new RuntimeException("startEpoch required!"))) {
                    throw new IllegalArgumentException("Event snapshot time must be before start time");
                }
            }

            if (event.getVotingPowerAsset().isEmpty()) {
                throw new IllegalArgumentException("Event voting power asset must be specified!");
            }
        }

        if (event.getVotingEventType() == USER_BASED) {
            if (event.getVotingPowerAsset().isPresent()) {
                throw new IllegalArgumentException("Event voting power asset must not be specified!");
            }
            if (event.getStartSlot().orElseThrow(() -> new RuntimeException("startSlot required!")) > event.getEndSlot().orElseThrow(() -> new RuntimeException("endSlot required!"))) {
                throw new IllegalArgumentException("Event start slot must be before end slot time!");
            }
            if (event.getStartEpoch().isPresent()) {
                throw new IllegalArgumentException("Event's start epoch must not be specified for USER_BASED voting event!");
            }
            if (event.getEndEpoch().isPresent()) {
                throw new IllegalArgumentException("Event's end epoch must not be specified for USER_BASED voting event!");
            }
            if (event.getSnapshotEpoch().isPresent()) {
                throw new IllegalArgumentException("Event's snapshot epoch must not be specified for USER_BASED voting event!");
            }

            for (var tally : event.getTallies()) {
                if (tally.getIndependentPartiesCount() <= 0) {
                    throw new IllegalArgumentException("Tally independent parties count must be greater than 0!");
                }

                if (tally.getType() == HYDRA) {
                    if (tally.getConfig() == null) {
                        throw new IllegalArgumentException("Hydra tally config must be specified!");
                    }
                    if (!(tally.getConfig() instanceof HydraTallyConfig)) {
                        throw new IllegalArgumentException("Hydra tally config must be specified!");
                    }
                    var hydraTallyConfig = (HydraTallyConfig) tally.getConfig();

                    if (hydraTallyConfig.getPartiesVerificationKeys().isEmpty()) {
                        throw new IllegalArgumentException("Hydra tally config must contain at least one party verification key!");
                    }
                }
            }
        }

    }

    public String submitCategory(CreateCategoryCommand category) {
        byte[] txData = l1TransactionCreator.submitCategory(category);

        return transactionSubmissionService.submitTransaction(txData);
    }

    public String submitTallyResults(CreateTallyResultCommand createTallyResultCommand) {
        byte[] txData = l1TransactionCreator.submitCentralisedTally(createTallyResultCommand);

        return transactionSubmissionService.submitTransaction(txData);
    }

}
