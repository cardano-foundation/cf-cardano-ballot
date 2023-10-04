package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VotingPowerService {

    private final BlockchainDataStakePoolService blockchainDataStakePoolService;

    public Either<Problem, Long> getVotingPower(Event event, String stakeAddress) {
        return switch (event.getVotingEventType()) {
            case USER_BASED -> {
                yield Either.left(Problem.builder()
                        .withTitle("VOTING_POWER_NOT_SUPPORTED")
                        .withDetail("Voting power not supported on USER_BASED events!")
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            case STAKE_BASED -> {
                var maybeAmount = blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress)
                        .filter(amount -> amount > 0);

                if (maybeAmount.isEmpty()) {
                    yield Either.left(Problem.builder()
                            .withTitle("STAKE_AMOUNT_NOT_AVAILABLE")
                            .withDetail("Stake amount not found (like wallet not staked) for event: " + event.getId() + " and stake address: " + stakeAddress)
                            .withStatus(NOT_FOUND)
                            .build()
                    );
                }

                yield Either.right(maybeAmount.orElseThrow());
            }
            case BALANCE_BASED -> {
                var maybeAmount = blockchainDataStakePoolService.getBalanceAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress)
                .filter(amount -> amount > 0);

                if (maybeAmount.isEmpty()) {
                    yield Either.left(Problem.builder()
                            .withTitle("BALANCE_AMOUNT_NOT_AVAILABLE")
                            .withDetail("Balance amount not found for event: " + event.getId() + " and stake address: " + stakeAddress)
                            .withStatus(NOT_FOUND)
                            .build());
                }

                yield Either.right(maybeAmount.orElseThrow());
            }
        };
    }

}
