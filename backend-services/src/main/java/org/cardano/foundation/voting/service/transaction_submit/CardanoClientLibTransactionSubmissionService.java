package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.stereotype.Service;

@Service
public class CardanoClientLibTransactionSubmissionService implements TransactionSubmissionService {

    public String submitTransaction(Event event, String rootHash) {
        // submit metadata transaction via blockfrost containing latest merkle root hash for this event
        // TODO - how do we make sure we are submitting, not somebody else???

        // TODO with yaci + CCL >= 0.5
        return null;
    }

}
