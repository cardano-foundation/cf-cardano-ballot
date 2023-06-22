package org.cardano.foundation.voting.service.merkle_tree;

import io.vavr.Value;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleTree;
import org.cardano.foundation.voting.domain.entity.Vote;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.MerkleTreeRepository;
import org.cardano.foundation.voting.repository.VoteMerkleProofRepository;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.transaction_submit.MerkleTreeRootsSubmissionService;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.cardanofoundation.merkle.MerkleElement;
import org.cardanofoundation.merkle.MerkleTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;
import static org.cardano.foundation.voting.domain.entity.Vote.VOTE_SERIALISER;

@Service
@Slf4j
public class VoteCommitmentService {

    @Autowired
    private MerkleTreeRepository merkleTreeRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private MerkleTreeRootsSubmissionService merkleTreeSubmissionService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private VoteMerkleProofRepository voteMerkleProofRepository;

    @Autowired
    private MerkleProofSerdeService merkleProofSerdeService;

    public void processVotesForAllEvents() {
        referenceDataService.findAllEvents().forEach(event -> {
            // this operation could be slow
            var allVotes = voteService.findAll(event); // TODO caching or paging or both? Maybe we use Redis???

            var merkleTreeRoot = MerkleTree.fromList(allVotes, VOTE_SERIALISER);

            var merkleRootHash = encodeHexString(merkleTreeRoot.itemHash());

            log.info("Merkle root: {}", merkleRootHash);

                // for a given event store on chain latest root hash
            var transactionHash = merkleTreeSubmissionService.submitMerkleTreesForAllEvents();
            // 36 hours to wait for L1 transaction to be confirmed // 100% sure

            // TODO rollback handling...?
            // should we ask user to repeat...??
            // verification app can show transaction confirmations count
            // we can react to rollback event and remove merkle proofs from the database

            // TODO we should probably store merkle proofs first for all votes we don't have merkle proofs yet
            // and then store merkle proofs for other votes as next priority
            generateAndStoreMerkleProofs(merkleTreeRoot, allVotes, transactionHash);

            // only if L1 transaction succeeds we store in our in merkle tree repository
            merkleTreeRepository.storeForEvent(event, new L1MerkleTree(merkleTreeRoot, merkleRootHash, transactionHash));
        });
    }

    private void generateAndStoreMerkleProofs(MerkleElement<Vote> merkleTree, List<Vote> votes, String l1TransactionHash) {
        log.info("Storing vote merkle proofs...");

        for (var vote : votes) {
            var maybeMerkleProof = MerkleTree.getProof(merkleTree, vote, VOTE_SERIALISER).map(Value::toJavaList);

            if (maybeMerkleProof.isEmpty()) {
                log.error("Merkle proof is empty for vote: {}, this should never happen", vote.getId());
                continue;
            }
            var proofItems = maybeMerkleProof.orElseThrow();

            var merkleRootHash = encodeHexString(merkleTree.itemHash());

            var proofItemsJson = merkleProofSerdeService.serialiseAsString(proofItems);

            var voteMerkleProof = VoteMerkleProof.builder()
                    .voteId(vote.getId())
                    .rootHash(merkleRootHash)
                    .proofItemsJson(proofItemsJson)
                    .l1TransactionHash(l1TransactionHash)
                    .absoluteSlot(0) // TODO read from yaci-store indexer or some other way via mini protocols
                    .blockHash("") // TODO read from yaci-store indexer or some other way via mini protocols
                    .build();

            voteMerkleProofRepository.saveAndFlush(voteMerkleProof);
        }

        log.info("Storing of merkle proofs finished.");
    }

}
