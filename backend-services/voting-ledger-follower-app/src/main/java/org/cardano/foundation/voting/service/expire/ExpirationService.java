package org.cardano.foundation.voting.service.expire;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpirationService {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    public boolean isEventActive(Event event) {
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.error("For the moment, there is no chain tip access!");

            return false;
        }

        var chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED ->  (epochNo >= event.getStartEpoch().orElseThrow() && epochNo <= event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getStartSlot().orElseThrow() && currentAbsoluteSlot <= event.getEndSlot().orElseThrow());
        };
    }

    public boolean isEventNotStarted(Event event) {
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.error("For the moment, there is no chain tip access!");

            return false;
        }

        var chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (epochNo < event.getStartEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot < event.getStartEpoch().orElseThrow());
        };
    }

    public boolean isEventFinished(Event event) {
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.warn("For the moment, there is no chain tip access!");

            return false;
        }

        var chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (epochNo > event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot > event.getEndSlot().orElseThrow());
        };
    }

}
