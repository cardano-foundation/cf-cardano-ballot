package org.cardano.foundation.voting.service.blockchain_state;

import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range.Bound;
import org.springframework.stereotype.Service;

@Service
public class SimpleSlotService implements SlotService {

    private final static int SLOT_BUFFER = 300; // 5 minutes since for now 1 slot = 1 second

    @Autowired
    private BlockchainDataService blockchainDataService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    public boolean isSlotExpired(long slot) {
        var currentAbsoluteSlot = blockchainDataService.getChainTip().getAbsoluteSlot();

        var range = Range
                .from(Bound.inclusive(currentAbsoluteSlot - SLOT_BUFFER))
                .to(Bound.inclusive(currentAbsoluteSlot + SLOT_BUFFER));

        return !range.contains(slot);
    }

}
