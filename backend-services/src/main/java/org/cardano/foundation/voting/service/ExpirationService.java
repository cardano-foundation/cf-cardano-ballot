package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.domain.entity.Event;

public interface ExpirationService {

    boolean isSlotExpired(long slot);

    boolean isEventInactive(Event event);

    boolean isEventActive(Event event);

    boolean isEventFinished(Event event);

}
