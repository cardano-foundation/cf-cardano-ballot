package org.cardano.foundation.voting.service.account;


import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultAccountService implements AccountService {

    @Autowired
    private BlockchainDataStakePoolService blockchainDataStakePoolService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Override
    public Either<Problem, Optional<Account>> findAccount(String eventName, String stakeAddress) {
        var maybeEvent = referenceDataService.findValidEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.orElseThrow();

        var stakeAddressE = stakeAddressVerificationService.checkStakeAddress(stakeAddress);
        if (stakeAddressE.isLeft()) {
            return Either.left(stakeAddressE.getLeft());
        }

        if (!List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            return Either.left(Problem.builder()
                    .withTitle("ONLY_STAKE_AND_BALANCE_BASED_EVENTS_SUPPORTED")
                    .withDetail("Only stake and balance based events are supported, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votingPower = blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch(), stakeAddress)
                .map(votingPowerAmount -> {
                    if (votingPowerAmount < 1_000_000L) {
                        return 0L;
                    }

                    return votingPowerAmount / 1_000_000L;
                });

        return Either.right(Optional.of(Account.builder()
                .stakeAddress(stakeAddress)
                .votingPower(votingPower.map(String::valueOf).orElse(null))
                .votingPowerAsset(ADA)
                .build())
        );
    }

}
