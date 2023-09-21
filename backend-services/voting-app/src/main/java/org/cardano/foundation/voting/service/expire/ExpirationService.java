package org.cardano.foundation.voting.service.expire;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpirationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Value("${expiration.slot.buffer}")
    private long expirationSlotBuffer;

    public boolean isSlotInRange(ChainFollowerClient.ChainTipResponse chainTip,
                                 long slot) {
        var currentAbsoluteSlot = chainTip.absoluteSlot();

        var range = Range
                .from(Range.Bound.inclusive(currentAbsoluteSlot - expirationSlotBuffer))
                .to(Range.Bound.inclusive(currentAbsoluteSlot + expirationSlotBuffer));

        return range.contains(slot);
    }

    public boolean isSlotExpired(ChainFollowerClient.ChainTipResponse chainTip,
                                 long slot) {
        return !isSlotInRange(chainTip, slot);
    }

}
