package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

@Service
public class ExpirationService {

    private final static int SLOT_BUFFER = 300; // 5 minutes since for now 1 slot = 1 second

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    public boolean isSlotExpired(long slot) {
        var currentAbsoluteSlot = blockchainDataChainTipService.getChainTip().getAbsoluteSlot();

        var range = Range
                .from(Range.Bound.inclusive(currentAbsoluteSlot - SLOT_BUFFER))
                .to(Range.Bound.inclusive(currentAbsoluteSlot + SLOT_BUFFER));

        return !range.contains(slot);
    }

    public boolean isEventInactive(Event event) {
        return !isEventActive(event);
    }

    public boolean isEventActive(Event event) {
        var chainTip = blockchainDataChainTipService.getChainTip();

        var absoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case USER_BASED -> (absoluteSlot >= event.getStartSlot() && absoluteSlot <= event.getEndSlot());
            case STAKE_BASED ->  (epochNo >= event.getStartEpoch() && epochNo <= event.getEndEpoch());
        };
    }

    public boolean isEventFinished(Event event) {
        var chainTip = blockchainDataChainTipService.getChainTip();

        var absoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case USER_BASED -> (absoluteSlot > event.getEndSlot());
            case STAKE_BASED -> (epochNo  > event.getEndEpoch());
        };
    }

}
