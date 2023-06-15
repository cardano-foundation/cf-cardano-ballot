package org.cardano.foundation.voting.service.transaction_submit;

import org.cardano.foundation.voting.domain.entity.Event;

public interface TransactionSubmissionService {

    void submitTransaction(Event event, String rootHash);

}
