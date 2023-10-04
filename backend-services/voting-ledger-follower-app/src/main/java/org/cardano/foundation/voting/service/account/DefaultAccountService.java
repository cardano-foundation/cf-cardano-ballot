package org.cardano.foundation.voting.service.account;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.vote.VotingPowerService;
import org.cardano.foundation.voting.utils.StakeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;

import static org.cardano.foundation.voting.domain.VotingEventType.BALANCE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.zalando.problem.Status.BAD_REQUEST;
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final ReferenceDataService referenceDataService;

    private final VotingPowerService votingPowerService;

    private final CardanoNetwork network;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Account> findAccount(String eventName, String stakeAddress) {
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

        var stakeAddressCheckE = StakeAddress.checkStakeAddress(network, stakeAddress);
        if (stakeAddressCheckE.isEmpty()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        if (!List.of(STAKE_BASED, BALANCE_BASED).contains(event.getVotingEventType())) {
            return Either.left(Problem.builder()
                    .withTitle("ONLY_STAKE_AND_BALANCE_BASED_EVENTS_SUPPORTED")
                    .withDetail("Only stake and balance based events are supported, event:" + event.getId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var amountE = votingPowerService.getVotingPower(event, stakeAddress);

        if (amountE.isEmpty()) {
            return Either.left(amountE.getLeft());
        }

        var amount = amountE.get();

        return Either.right(Account.builder()
                .stakeAddress(stakeAddress)
                .network(network)
                .epochNo(event.getSnapshotEpoch().orElseThrow())
                .votingPower(String.valueOf(amount))
                .votingPowerAsset(event.getVotingPowerAsset().orElseThrow())
                .build()
        );
    }

}
