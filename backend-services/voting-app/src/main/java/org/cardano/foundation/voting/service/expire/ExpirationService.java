package org.cardano.foundation.voting.service.expire;

import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

@Service
public class ExpirationService {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Value("${expiration.slot.buffer}")
    private long expirationSlotBuffer;

    public boolean isSlotInRange(long slot) {
        var currentAbsoluteSlot = blockchainDataChainTipService.getChainTip().getAbsoluteSlot();

        var range = Range
                .from(Range.Bound.inclusive(currentAbsoluteSlot - expirationSlotBuffer))
                .to(Range.Bound.inclusive(currentAbsoluteSlot + expirationSlotBuffer));

        return range.contains(slot);
    }

    public boolean isSlotExpired(long slot) {
        return !isSlotInRange(slot);
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
            case STAKE_BASED, BALANCE_BASED ->  (epochNo >= event.getStartEpoch() && epochNo <= event.getEndEpoch());
        };
    }

    public boolean isEventFinished(Event event) {
        var chainTip = blockchainDataChainTipService.getChainTip();

        var absoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case USER_BASED -> (absoluteSlot > event.getEndSlot());
            case STAKE_BASED, BALANCE_BASED -> (epochNo  > event.getEndEpoch());
        };
    }

}
