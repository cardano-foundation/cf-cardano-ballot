package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.domain.entity.Event;

public interface TransactionSubmissionService {

    void submitTransaction(Event event, String rootHash);

}
