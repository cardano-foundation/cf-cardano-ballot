package org.cardano.foundation.voting.service.expire;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.domain.ExpirationData;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpirationService {

    private final BlockchainDataChainTipService blockchainDataChainTipService;

    private boolean isEventActive(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED ->  (epochNo >= event.getStartEpoch().orElseThrow() && epochNo <= event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getStartSlot().orElseThrow() && currentAbsoluteSlot <= event.getEndSlot().orElseThrow());
        };
    }

    private boolean isEventNotStarted(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var currentEpochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (currentEpochNo < event.getStartEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot < event.getStartSlot().orElseThrow());
        };
    }

    private boolean isEventFinished(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var currentEpochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (currentEpochNo > event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot > event.getEndSlot().orElseThrow());
        };
    }

    private boolean isProposalsReveal(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var currentEpochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (currentEpochNo > event.getProposalsRevealEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot > event.getProposalsRevealSlot().orElseThrow());
        };
    }

    public Either<Problem, List<ExpirationData>> getExpirationDataList(List<Event> events) {
        var chainTipE = blockchainDataChainTipService.getChainTip();

        if (chainTipE.isEmpty()) {
            log.warn("For the moment, there is no chain tip access, problem:{}", chainTipE.getLeft());

            return Either.left(Problem.builder()
                    .withTitle("CHAIN_FOLLOWER_ERROR")
                    .withDetail("Chain-Follower service error.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        List<ExpirationData> expirationDataList = events.stream().map(e -> {
                    var chainTip = chainTipE.get();

                    boolean eventNotStarted = isEventNotStarted(e, chainTip);
                    boolean eventActive = isEventActive(e, chainTip);
                    boolean eventFinished = isEventFinished(e, chainTip);
                    boolean proposalsReveal = isProposalsReveal(e, chainTip);

                    return new ExpirationData(
                            e.getId(),
                            eventNotStarted,
                            eventFinished,
                            eventActive,
                            proposalsReveal
                    );
                })
                .toList();

        return Either.right(expirationDataList);
    }

    public Either<Problem, ExpirationData> getExpirationData(Event event) {
        var chainTipE = blockchainDataChainTipService.getChainTip();

        if (chainTipE.isEmpty()) {
            log.warn("For the moment, there is no chain tip access, problem:{}", chainTipE.getLeft());

            return Either.left(Problem.builder()
                    .withTitle("CHAIN_FOLLOWER_ERROR")
                    .withDetail("Chain-Follower service error.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }

        var chainTip = chainTipE.get();

        boolean eventNotStarted = isEventNotStarted(event, chainTip);
        boolean eventActive = isEventActive(event, chainTip);
        boolean eventFinished = isEventFinished(event, chainTip);
        boolean proposalsReveal = isProposalsReveal(event, chainTip);

        var expirationData = new ExpirationData(
                event.getId(),
                eventNotStarted,
                eventFinished,
                eventActive,
                proposalsReveal
        );

        return Either.right(expirationData);
    }

}
