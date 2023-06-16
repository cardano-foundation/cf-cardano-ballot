package org.cardano.foundation.voting.service.account;


import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class DefaultAccountService implements AccountService {

    @Autowired
    private BlockchainDataService blockchainDataService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Override
    public Either<Problem, Optional<Account>> findAccount(String networkName, String eventName, String stakeAddress) {
        var maybeNetwork = CardanoNetwork.fromName(networkName);
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", networkName);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var network = maybeNetwork.orElseThrow();

        if (network != this.cardanoNetwork) {
            return Either.left(Problem.builder()
                    .withTitle("WRONG_NETWORK")
                    .withDetail("Backend configured with network:" + this.cardanoNetwork)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        var maybeEvent = referenceDataService.findEventByName(eventName);
        if (maybeEvent.isEmpty()) {
            log.warn("Unrecognised event, eventName:{}", eventName);

            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        var event = maybeEvent.orElseThrow();

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
