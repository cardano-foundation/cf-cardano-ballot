package org.cardano.foundation.voting.service.expire;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpirationService {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Value("${expiration.slot.buffer}")
    private long expirationSlotBuffer;

    public boolean isSlotInRange(long slot) {
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.warn("Slot maybe NOT expired but we have no way to check this since we have no chain tip access.");

            return false;
        }

        var currentAbsoluteSlot = chainTipE.get().getAbsoluteSlot();

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
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.warn("Slot maybe NOT expired but we have no way to check this since we have no chain tip access.");

            return false;
        }

        ChainTip chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED ->  (epochNo >= event.getStartEpoch().orElseThrow() && epochNo <= event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot >= event.getStartSlot().orElseThrow() && currentAbsoluteSlot <= event.getEndSlot().orElseThrow());
        };
    }

    public boolean isEventFinished(Event event) {
        var chainTipE = blockchainDataChainTipService.getChainTip();
        if (chainTipE.isEmpty()) {
            log.warn("Slot maybe NOT expired but we have no way to check this since we have no chain tip access.");

            return false;
        }

        ChainTip chainTip = chainTipE.get();
        var currentAbsoluteSlot = chainTip.getAbsoluteSlot();
        var epochNo = chainTip.getEpochNo();

        return switch (event.getVotingEventType()) {
            case STAKE_BASED, BALANCE_BASED -> (epochNo > event.getEndEpoch().orElseThrow());
            case USER_BASED -> (currentAbsoluteSlot > event.getEndSlot().orElseThrow());
        };
    }

}
