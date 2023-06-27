package org.cardano.foundation.voting.service.account;


import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultAccountService implements AccountService {

    @Autowired
    private BlockchainDataStakePoolService blockchainDataStakePoolService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Override
    public Either<Problem, Optional<Account>> findAccount(String eventName, String stakeAddress) {
        var maybeEvent = referenceDataService.findEventById(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.orElseThrow();

        if (event.getVotingEventType() != VotingEventType.STAKE_BASED) {
            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_STAKE_BASED")
                    .withDetail("Event is not stake based, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votingPower = blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch(), stakeAddress);

        return Either.right(Optional.of(Account.builder()
                .stakeAddress(stakeAddress)
                .votingPower(votingPower.orElse(null))
                .build()
                )
        );
    }

}
