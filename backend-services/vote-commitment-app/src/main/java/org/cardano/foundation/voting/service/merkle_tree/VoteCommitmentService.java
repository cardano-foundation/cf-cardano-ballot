package org.cardano.foundation.voting.service.merkle_tree;

import io.vavr.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.L1SubmissionData;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.MerkleTree;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.bloxbean.cardano.client.util.HexUtil.encodeHexString;
import static org.cardano.foundation.voting.domain.VoteSerialisations.VOTE_SERIALISER;
import static org.cardano.foundation.voting.domain.VoteSerialisations.createSerialiserFunction;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteCommitmentService {

    private final VoteService voteService;

    private final L1SubmissionService l1SubmissionService;

    private final ChainFollowerClient chainFollowerClient;

    private final VoteMerkleProofService voteMerkleProofService;

    private final MerkleProofSerdeService merkleProofSerdeService;

    private final JsonService jsonService;

    public void processVotesForAllEvents() {
        val l1MerkleCommitments = getValidL1MerkleCommitments();
        if (l1MerkleCommitments.isEmpty()) {
            log.info("No l1 commitments to process.");
            return;
        }

        // Event maybe active but it makes no sense spamming L1 when there are no signedVotes to process
        if (l1MerkleCommitments.stream().allMatch(l1MerkleCommitment -> l1MerkleCommitment.signedVotes().isEmpty())) {
            log.info("No signedVotes to process.");
            return;
        }

        val l1TransactionDataE = l1SubmissionService.submitMerkleCommitments(l1MerkleCommitments);
        if (l1TransactionDataE.isEmpty()) {
            val problem = l1TransactionDataE.swap().get();

            log.error("Transaction submission failed, problem:{}, will try to submit again in some time...", problem.toString());
            return;
        }

        val l1SubmissionData = l1TransactionDataE.get();

        generateAndStoreMerkleProofs(l1MerkleCommitments, l1SubmissionData);
    }

    private List<L1MerkleCommitment> getValidL1MerkleCommitments() {
        val allCommitmentWindowOpenEventsE = chainFollowerClient.findAllCommitmentWindowOpenEvents();


        if (allCommitmentWindowOpenEventsE.isEmpty()) {
            val issue = allCommitmentWindowOpenEventsE.swap().get();

            log.error("Failed to get open window eventSummaries issue:{}, will try again in some time...", issue.toString());

            return List.of();
        }

        val eventsToProcess1 = allCommitmentWindowOpenEventsE.get();

        log.info("Found events with active commitments window: {}", eventsToProcess1.stream()
                .map(ChainFollowerClient.EventSummary::id)
                .toList());

        val allFinishedEventsWithClosedCommitmentWindowE = chainFollowerClient.findAllEndedEventsWithoutOpenCommitmentWindow();

        if (allFinishedEventsWithClosedCommitmentWindowE.isEmpty()) {
            val issue = allFinishedEventsWithClosedCommitmentWindowE.swap().get();

            log.error("Failed to get finished and with close window eventSummaries issue :{}, will try again in some time...", issue.toString());

            return List.of();
        }

        val allFinishedEventsWithClosedCommitmentWindow = allFinishedEventsWithClosedCommitmentWindowE.get();

        val eventsToProcess2 = allFinishedEventsWithClosedCommitmentWindow.stream()
                // if we find at least one vote merkle proof which is not validated
                .filter(eventSummary -> !voteMerkleProofService.findTop1InvalidatedByEventId(eventSummary.id()).isEmpty())
                .toList();

        val allEventsToProcess = Stream.concat(
                        eventsToProcess1.stream(),
                        eventsToProcess2.stream())
                .toList();

        return allEventsToProcess.stream()
                .map(event -> {
                    // TODO caching or paging or both or neither? Maybe we use Redis???
                    log.info("Loading signedVotes from db for event:{}", event.id());
                    val stopWatch = new StopWatch();
                    stopWatch.start();

                    val votes = voteService.findAllCompactVotesByEventId(event.id());

                    stopWatch.stop();
                    log.info("Loaded signedVotes, count:{}, time: {} secs", votes.size(), stopWatch.getTotalTimeSeconds());

                    val root = MerkleTree.fromList(votes, VOTE_SERIALISER);

                    return new L1MerkleCommitment(votes, root, event.id());
                })
                .toList();
    }

    private void generateAndStoreMerkleProofs(List<L1MerkleCommitment> l1MerkleCommitments,
                                              L1SubmissionData l1SubmissionData) {
        log.info("Storing vote merkle proofs...");

        for (val l1MerkleCommitment : l1MerkleCommitments) {
            val root = l1MerkleCommitment.root();

            log.info("Storing merkle proofs for event: {}", l1MerkleCommitment.eventId());

            val storeProofsStartStop = new StopWatch();
            storeProofsStartStop.start();

            for (val vote : l1MerkleCommitment.signedVotes()) {
                val maybeMerkleProof = MerkleTree.getProof(root, vote, createSerialiserFunction()).map(Value::toJavaList);
                if (maybeMerkleProof.isEmpty()) {
                    log.error("Merkle proof is empty for vote: {}, this should never ever happen", vote.getSignature());
                    throw new RuntimeException("Merkle proof is empty for vote: " + vote.getSignature());
                }
                val proofItems = maybeMerkleProof.orElseThrow();
                val merkleRootHash = encodeHexString(root.itemHash());

                val walletType = vote.getWalletType();

                switch (walletType) {
                    case KERI -> handleKeriWalletType(vote, proofItems, merkleRootHash, l1SubmissionData);
                    case CARDANO -> handleCardanoWalletType(vote, proofItems, merkleRootHash, l1SubmissionData);
                }

                log.info("Storing vote merkle proofs for all events completed.");
            }

            storeProofsStartStop.stop();

            log.info("Storing merkle proofs: {}, completed for event: {}, time: {} secs", l1MerkleCommitment.signedVotes().size(), l1MerkleCommitment.eventId(), storeProofsStartStop.getTotalTimeSeconds());
        }
    }

    private void handleKeriWalletType(VoteRepository.CompactVote vote,
                                      List<ProofItem> proofItems,
                                      String merkleRootHash,
                                      L1SubmissionData l1SubmissionData) {
        // TODO KERI signature validation?

        val payloadM = vote.getPayload();
        if (payloadM.isEmpty()) {
            log.error("Payload is empty for KERI vote:{}", vote.getSignature());
            return;
        }

        val payload =  payloadM.orElseThrow();

        val keriVoteEnvelopeE = jsonService.decodeKERIVoteEnvelope(payload);
        if (keriVoteEnvelopeE.isEmpty()) {
            log.error("Invalid KERI vote payload for vote:{}", vote.getSignature());
            return;
        }

        val keriVoteEnvelope = keriVoteEnvelopeE.get();

        val voteId = keriVoteEnvelope.getData().getId();

        val proofItemsJson = merkleProofSerdeService.serialiseAsString(proofItems);
        val eventId = keriVoteEnvelope.getData().getEvent();

        val voteMerkleProof = VoteMerkleProof.builder()
                .voteId(voteId)
                .voteIdNumericHash(UUID.fromString(voteId).hashCode() & 0xFFFFFFF)
                .eventId(eventId)
                .rootHash(merkleRootHash)
                .absoluteSlot(l1SubmissionData.slot())
                .proofItemsJson(proofItemsJson)
                .l1TransactionHash(l1SubmissionData.txHash())
                .invalidated(false)
                .build();

        voteMerkleProofService.store(voteMerkleProof);
    }

    private void handleCardanoWalletType(VoteRepository.CompactVote vote,
                                         List<ProofItem> proofItems,
                                         String merkleRootHash,
                                         L1SubmissionData l1SubmissionData) {
        val cip30Verifier = new CIP30Verifier(vote.getSignature(), vote.getPublicKey());

        val cip30VerificationResult = cip30Verifier.verify();
        if (!cip30VerificationResult.isValid()) {
            log.error("Invalid CIP 30 signature for vote:{}", vote.getSignature());
            return;
        }

        val voteSignedJsonPayload = cip30VerificationResult.getMessage(TEXT);

        val cip93EnvelopeE = jsonService.decodeCIP93VoteEnvelope(voteSignedJsonPayload);

        if (cip93EnvelopeE.isEmpty()) {
            log.error("Invalid voteSignedJsonPayload for vote:{}", vote.getSignature());
            return;
        }
        val voteEnvelopeCIP93Envelope = cip93EnvelopeE.get();
        val voteId = voteEnvelopeCIP93Envelope.getData().getId();

        val proofItemsJson = merkleProofSerdeService.serialiseAsString(proofItems);

        val voteMerkleProof = VoteMerkleProof.builder()
                .voteId(voteId)
                .voteIdNumericHash(UUID.fromString(voteId).hashCode() & 0xFFFFFFF)
                .eventId(voteEnvelopeCIP93Envelope.getData().getEvent())
                .rootHash(merkleRootHash)
                .absoluteSlot(l1SubmissionData.slot())
                .proofItemsJson(proofItemsJson)
                .l1TransactionHash(l1SubmissionData.txHash())
                .invalidated(false)
                .build();

        voteMerkleProofService.store(voteMerkleProof);
    }

}
