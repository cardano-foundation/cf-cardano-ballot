package org.cardano.foundation.voting.service;


import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private BlockchainDataService blockchainDataService;

    @Autowired
    private ReferenceDataService referenceDataService;

    public Either<Problem, Optional<Account>> findAccount(String networkName, String eventName, String stakeAddress) {
        var maybeNetwork = Network.fromName(networkName);
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", networkName);

            return Either.left(Problem.builder()
                    .withTitle("Invalid network, supported networks:" + Network.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        var maybeEvent = referenceDataService.findEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.get();

        var votingPowerE = blockchainDataService.getVotingPower(network, event.getSnapshotEpoch(), stakeAddress);
        if (votingPowerE.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var maybeVotingPower = votingPowerE.get();

        return Either.right(Optional.of(Account.builder()
                .stakeAddress(stakeAddress)
                .votingPower(maybeVotingPower.orElse(0L))
                .build()
                )
        );
    }

}
