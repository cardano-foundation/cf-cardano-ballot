package org.cardano.foundation.voting.service.blockchain_state;

public interface SlotService {

    boolean isSlotExpired(long slot);

}
