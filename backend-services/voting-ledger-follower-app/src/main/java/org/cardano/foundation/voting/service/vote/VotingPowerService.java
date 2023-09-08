package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class VotingPowerService {

    private final BlockchainDataStakePoolService blockchainDataStakePoolService;

    public Either<Problem, Optional<Long>> getVotingPower(Event event, String stakeAddress) {
        return switch (event.getVotingEventType()) {
            case USER_BASED -> {
                yield Either.left(Problem.builder()
                        .withTitle("VOTING_POWER_NOT_SUPPORTED")
                        .withDetail("Voting power not supported on USER_BASED events!")
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            case STAKE_BASED -> {
                yield Either.right(blockchainDataStakePoolService.getStakeAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress));
            }
            case BALANCE_BASED -> {
                yield Either.right(blockchainDataStakePoolService.getBalanceAmount(event.getSnapshotEpoch().orElseThrow(), stakeAddress));
            }
        };
    }

}
