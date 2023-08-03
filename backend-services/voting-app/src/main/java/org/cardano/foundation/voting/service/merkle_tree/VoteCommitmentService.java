package org.cardano.foundation.voting.service.merkle_tree;

import io.vavr.Value;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.cardano.foundation.voting.service.vote.VoteService;
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
    private MerkleTreeService merkleTreeService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private L1SubmissionService l1SubmissionService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private MerkleProofSerdeService merkleProofSerdeService;

    public void processVotesForAllEvents() {
        var l1MerkleCommitments = getL1MerkleCommitments();
        if (l1MerkleCommitments.isEmpty()) {
            log.info("No l1 commitments to process.");
            return;
        }

        // Event maybe active but it makes no sense spamming L1 when there are no votes to process
        if (l1MerkleCommitments.stream().allMatch(l1MerkleCommitment -> l1MerkleCommitment.votes().isEmpty())) {
            log.info("No votes to process.");
            return;
        }

        var l1TransactionDataE = l1SubmissionService.submitMerkleCommitments(l1MerkleCommitments);
        if (l1TransactionDataE.isEmpty()) {
            log.error("Transaction submission failed, issue:{}, will try to submit again in some time...", l1TransactionDataE.swap().get());
            return;
        }

        var l1SubmissionData = l1TransactionDataE.get();
        var l1TransactionHash = l1SubmissionData.txHash();
        var l1TransactionSlot = l1SubmissionData.slot();

        // we only need hash, we don't need to wait for transaction to be confirmed
        // voter's receipt will contain merkle proof confirmation data
        merkleTreeService.storeAll(l1MerkleCommitments, l1TransactionHash, l1TransactionSlot);

        generateAndStoreMerkleProofs(l1MerkleCommitments, l1TransactionHash, l1TransactionSlot);
    }

    private List<L1MerkleCommitment> getL1MerkleCommitments() {
        return referenceDataService.findAllActiveEvents().stream()
                .map(event -> {
                    // TODO caching or paging or both? Maybe we use Redis???
                    var allVotes = voteService.findAll(event);

                    var root = MerkleTree.fromList(allVotes, VOTE_SERIALISER);

                    return new L1MerkleCommitment(allVotes, root, event);
                })
                .toList();
    }

    private void generateAndStoreMerkleProofs(List<L1MerkleCommitment> l1MerkleCommitments, String l1TransactionHash, long l1TransactionSlot) {
        log.info("Storing vote merkle proofs...");

        for (var l1MerkleCommitment : l1MerkleCommitments) {
            log.info("Storing merkle proofs for event: {}", l1MerkleCommitment.event().getId());

            var root = l1MerkleCommitment.root();

            for (var vote : l1MerkleCommitment.votes()) {
                var maybeMerkleProof = MerkleTree.getProof(root, vote, VOTE_SERIALISER).map(Value::toJavaList);
                if (maybeMerkleProof.isEmpty()) {
                    log.error("Merkle proof is empty for vote: {}, this should never ever happen", vote.getId());
                    throw new RuntimeException("Merkle proof is empty for vote: " + vote.getId());
                }
                var proofItems = maybeMerkleProof.orElseThrow();
                var merkleRootHash = encodeHexString(root.itemHash());

                var proofItemsJson = merkleProofSerdeService.serialiseAsString(proofItems);

                var voteMerkleProof = VoteMerkleProof.builder()
                        .voteId(vote.getId())
                        .eventId(vote.getEventId())
                        .rootHash(merkleRootHash)
                        .absoluteSlot(l1TransactionSlot)
                        .proofItemsJson(proofItemsJson)
                        .l1TransactionHash(l1TransactionHash)
                        .invalidated(false)
                        .build();

                voteMerkleProofService.store(voteMerkleProof);
            }

            log.info("Storing merkle proofs completed for event: {}", l1MerkleCommitment.event().getId());
        }

        log.info("Storing vote merkle proofs completed.");
    }

}
