package org.cardano.foundation.voting.service.expire;

import io.vavr.control.Either;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventAdditionalInfoService {

    private final BlockchainDataChainTipService blockchainDataChainTipService;

    @Value("${voting.event.additional.commitments.delay.slots:3600}")
    private long additionalCommitmentsDelaySlots;

    @Value("${voting.event.additional.commitments.delay.epochs:1}")
    private long additionalCommitmentsDelayEpochs;

    @PostConstruct
    public void init() {
        log.info("EventAdditionalInfoService initialised with" +
                        " additionalCommitmentsDelaySlots:{}," +
                        " additionalCommitmentsDelayEpochs:{}",

                additionalCommitmentsDelaySlots, additionalCommitmentsDelayEpochs);
    }

    private boolean isEventActive(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED ->  (epochNo >= event.getStartEpoch().orElseThrow() && epochNo <= event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getStartSlot().orElseThrow() && currentAbsoluteSlot <= event.getEndSlot().orElseThrow());
        };
    }

    private boolean isEventNotStarted(Event event, ChainTip chainTip) {
        return !isEventStarted(event, chainTip);
    }

    private boolean isEventStarted(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var currentEpochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (currentEpochNo >= event.getStartEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getStartSlot().orElseThrow());
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
            case STAKE_BASED, BALANCE_BASED -> (currentEpochNo >= event.getProposalsRevealEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getProposalsRevealSlot().orElseThrow());
        };
    }

    private boolean isCommitmentsWindowOpen(Event event, ChainTip chainTip) {
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var currentEpochNo = chainTip.getEpochNo();

        var isEventStarted = isEventStarted(event, chainTip);

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> {
                var endEpoch = event.getEndEpoch().orElseThrow();
                var endEpochWithDelay = endEpoch + additionalCommitmentsDelayEpochs;

                yield isEventStarted && (currentEpochNo <= endEpochWithDelay);
            }
            case USER_BASED -> {
                var endSlot = event.getEndSlot().orElseThrow();
                var endSlotWithDelay = endSlot + additionalCommitmentsDelaySlots;

                yield isEventStarted && (currentAbsoluteSlot <= endSlotWithDelay);
            }
        };
    }

    public Either<Problem, List<EventAdditionalInfo>> getEventAdditionalInfo(List<Event> events) {
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

        var eventAdditionalInfoList = events.stream().map(event -> {
                    boolean eventNotStarted = isEventNotStarted(event, chainTip);
                    boolean eventStarted = isEventStarted(event, chainTip);

                    boolean eventActive = isEventActive(event, chainTip);
                    boolean eventFinished = isEventFinished(event, chainTip);
                    boolean proposalsReveal = isProposalsReveal(event, chainTip);
                    boolean commitmentsWindowOpen = isCommitmentsWindowOpen(event, chainTip);

                    return new EventAdditionalInfo(
                            event.getId(),
                            eventNotStarted,
                            eventStarted,
                            eventFinished,
                            eventActive,
                            proposalsReveal,
                            commitmentsWindowOpen
                    );
                })
                .toList();

        return Either.right(eventAdditionalInfoList);
    }

    public Either<Problem, EventAdditionalInfo> getEventAdditionalInfo(Event event) {
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
        boolean eventStarted = isEventStarted(event, chainTip);

        boolean eventActive = isEventActive(event, chainTip);
        boolean eventFinished = isEventFinished(event, chainTip);
        boolean proposalsReveal = isProposalsReveal(event, chainTip);
        boolean commitmentsWindowOpen = isCommitmentsWindowOpen(event, chainTip);

        return Either.right(new EventAdditionalInfo(
                event.getId(),
                eventNotStarted,
                eventStarted,
                eventFinished,
                eventActive,
                proposalsReveal,
                commitmentsWindowOpen
        ));
    }

}
