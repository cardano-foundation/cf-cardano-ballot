package org.cardano.foundation.voting.service.blockchain_state;

import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.entity.Event;

import java.util.List;
import java.util.Optional;

public interface BlockchainDataService {

    ChainTip getChainTip();

    long getVotingPower(CardanoNetwork network, int snapshotEpoch, String stakeAddress);

    Optional<String> getLastMerkleRootHashes(Event event);

    List<String> getMerkleRootHashes(Event event);

    Optional<TransactionDetails> getTransactionDetails(String transactionHash);

}
