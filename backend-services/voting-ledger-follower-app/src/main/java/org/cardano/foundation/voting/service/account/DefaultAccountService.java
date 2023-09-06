package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.vote.VotingPowerService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.Account.AccountStatus.ELIGIBLE;
import static org.cardano.foundation.voting.domain.Account.AccountStatus.NOT_ELIGIBLE;
import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final ReferenceDataService referenceDataService;

    private final VotingPowerService votingPowerService;

    private final StakeAddressVerificationService stakeAddressVerificationService;

    private final CardanoNetwork network;

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

        var stakeAddressCheckE = stakeAddressVerificationService.checkStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        if (!List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            return Either.left(Problem.builder()
                    .withTitle("ONLY_STAKE_AND_BALANCE_BASED_EVENTS_SUPPORTED")
                    .withDetail("Only stake and balance based events are supported, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var votingPower = votingPowerService.getVotingPower(event, stakeAddress);

        return Either.right(Optional.of(Account.builder()
                .stakeAddress(stakeAddress)
                .network(network)
                .accountStatus(votingPower.map(vp -> vp > 0 ? ELIGIBLE : NOT_ELIGIBLE).orElse(NOT_ELIGIBLE))
                .epochNo(event.getSnapshotEpoch().orElseThrow())
                .votingPower(votingPower.map(String::valueOf))
                .votingPowerAsset(event.getVotingPowerAsset())
                .build())
        );
    }

}
