package org.cardano.foundation.voting.service.blockchain_state;

import org.cardano.foundation.voting.domain.TransactionDetails;

import java.util.Optional;

public interface BlockchainDataTransactionDetailsService {

    Optional<TransactionDetails> getTransactionDetails(String transactionHash);

}
