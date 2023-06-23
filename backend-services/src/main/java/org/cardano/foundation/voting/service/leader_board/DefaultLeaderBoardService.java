package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.service.ExpirationService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class DefaultLeaderBoardService implements LeaderBoardService {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Override
    public Either<Problem, Leaderboard> getLeaderboard(String networkName, String eventName) {
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

        var maybeEvent = referenceDataService.findEventById(eventName);
        if (maybeEvent.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, eventName:" + eventName)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var event = maybeEvent.orElseThrow();

        if (!expirationService.isEventFinished(event)) {
            return Either.left(Problem.builder()
                    .withTitle("LEADER_BOARD_NOT_AVAILABLE")
                    .withDetail("Voting not finished yet, event:" + event.getId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        // TODO: implement this
        return Either.right(Leaderboard.builder().build());
    }

}
