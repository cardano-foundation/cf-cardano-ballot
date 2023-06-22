package org.cardano.foundation.voting.service.transaction_submit;

import org.cardano.foundation.voting.repository.MerkleTreeRepository;
import org.cardano.foundation.voting.service.ExpirationService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardanoClientLibMerkleTreeRootsSubmissionService implements MerkleTreeRootsSubmissionService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private MerkleTreeRepository merkleTreeRepository;

    public String submitMerkleTreesForAllEvents() {
        referenceDataService.findAllEvents().stream()
                .filter(event -> expirationService.isEventActive(event))
                .forEach(event -> {
                    var maybeMerkleTree = merkleTreeRepository.findByEvent(event);
                    if (maybeMerkleTree.isPresent()) {
                        var merkleTree = maybeMerkleTree.orElseThrow();
                        var rootHash = merkleTree.getRootHash();
                        var eventName = event.getName();

                        // TODO should we tally?
                        // TODO should we send to L1 individual votes

                        // prepare metadata transaction and submit via Cardano Client Lib

                    }
                });

        // submit metadata transaction via blockfrost containing latest merkle root hash for this event
        // TODO - how do we make sure we are submitting, not somebody else???

        // TODO with yaci + CCL >= 0.5
        return null;
    }

}
