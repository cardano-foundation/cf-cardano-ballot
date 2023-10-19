package org.cardano.foundation.voting.service.merkle_tree;

import io.vavr.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.L1SubmissionData;
import org.cardano.foundation.voting.domain.entity.VoteMerkleProof;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.cardano.foundation.voting.service.vote.VoteService;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.MerkleTree;
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
        var l1MerkleCommitments = getValidL1MerkleCommitments();
        if (l1MerkleCommitments.isEmpty()) {
            log.info("No l1 commitments to process.");
            return;
        }

        // Event maybe active but it makes no sense spamming L1 when there are no signedVotes to process
        if (l1MerkleCommitments.stream().allMatch(l1MerkleCommitment -> l1MerkleCommitment.signedVotes().isEmpty())) {
            log.info("No signedVotes to process.");
            return;
        }

        var l1TransactionDataE = l1SubmissionService.submitMerkleCommitments(l1MerkleCommitments);
        if (l1TransactionDataE.isEmpty()) {
            var problem = l1TransactionDataE.swap().get();

            log.error("Transaction submission failed, problem:{}, will try to submit again in some time...", problem.toString());
            return;
        }

        var l1SubmissionData = l1TransactionDataE.get();

        generateAndStoreMerkleProofs(l1MerkleCommitments, l1SubmissionData);
    }

    private List<L1MerkleCommitment> getValidL1MerkleCommitments() {
        var allCommitmentWindowOpenEventsE = chainFollowerClient.findAllCommitmentWindowOpenEvents();


        if (allCommitmentWindowOpenEventsE.isEmpty()) {
            var issue = allCommitmentWindowOpenEventsE.swap().get();

            log.error("Failed to get open window eventSummaries issue:{}, will try again in some time...", issue.toString());

            return List.of();
        }

        var eventsToProcess1 = allCommitmentWindowOpenEventsE.get();

        log.info("Found events with active commitments window: {}", eventsToProcess1.stream()
                .map(ChainFollowerClient.EventSummary::id)
                .toList());

        var allFinishedEventsWithClosedCommitmentWindowE = chainFollowerClient.findAllEndedEventsWithoutOpenCommitmentWindow();

        if (allFinishedEventsWithClosedCommitmentWindowE.isEmpty()) {
            var issue = allFinishedEventsWithClosedCommitmentWindowE.swap().get();

            log.error("Failed to get finished and with close window eventSummaries issue :{}, will try again in some time...", issue.toString());

            return List.of();
        }

        var allFinishedEventsWithClosedCommitmentWindow = allFinishedEventsWithClosedCommitmentWindowE.get();

        List<ChainFollowerClient.EventSummary> eventsToProcess2 = allFinishedEventsWithClosedCommitmentWindow.stream()
                .filter(eventSummary -> !voteMerkleProofService.findTop1InvalidatedByEventId(eventSummary.id()).isEmpty())
                .toList();

        var allEventsToProcess = Stream.concat(
                eventsToProcess1.stream(),
                eventsToProcess2.stream())
                .toList();

        return allEventsToProcess.stream()
                .map(event -> {
                    // TODO caching or paging or both or neither? Maybe we use Redis???
                    log.info("Loading signedVotes from db for event:{}", event.id());
                    var stopWatch = new StopWatch();
                    stopWatch.start();

                    var votes = voteService.findAllCompactVotesByEventId(event.id())
                            .stream()
                            .filter(signedWeb3Request -> {
                                var cip30Result = new CIP30Verifier(signedWeb3Request.getCoseSignature(), signedWeb3Request.getCosePublicKey());

                                return cip30Result.verify().isValid();
                            })
                            .toList();

                    stopWatch.stop();
                    log.info("Loaded signedVotes, count:{}, time: {} secs", votes.size(), stopWatch.getTotalTimeSeconds());

                    var root = MerkleTree.fromList(votes, VOTE_SERIALISER);

                    return new L1MerkleCommitment(votes, root, event.id());
                })
                .toList();
    }

    private void generateAndStoreMerkleProofs(List<L1MerkleCommitment> l1MerkleCommitments,
                                              L1SubmissionData l1SubmissionData) {
        log.info("Storing vote merkle proofs...");

        for (var l1MerkleCommitment : l1MerkleCommitments) {
            var root = l1MerkleCommitment.root();

            log.info("Storing merkle proofs for event: {}", l1MerkleCommitment.eventId());

            var storeProofsStartStop = new StopWatch();
            storeProofsStartStop.start();
            for (var vote : l1MerkleCommitment.signedVotes()) {
                var maybeMerkleProof = MerkleTree.getProof(root, vote, createSerialiserFunction()).map(Value::toJavaList);
                if (maybeMerkleProof.isEmpty()) {
                    log.error("Merkle proof is empty for vote: {}, this should never ever happen", vote.getCoseSignature());
                    throw new RuntimeException("Merkle proof is empty for vote: " + vote.getCoseSignature());
                }
                var proofItems = maybeMerkleProof.orElseThrow();
                var merkleRootHash = encodeHexString(root.itemHash());

                var proofItemsJson = merkleProofSerdeService.serialiseAsString(proofItems);

                var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());

                var cip30VerificationResult = cip30Verifier.verify();
                if (!cip30VerificationResult.isValid()) {
                    log.error("Invalid CIP 30 signature for vote:{}", vote.getCoseSignature());
                    continue;
                }

                var voteSignedJsonPayload = cip30VerificationResult.getMessage(TEXT);

                var cip93EnvelopeE = jsonService.decodeCIP93VoteEnvelope(voteSignedJsonPayload);

                if (cip93EnvelopeE.isEmpty()) {
                    log.error("Invalid voteSignedJsonPayload for vote:{}", vote.getCoseSignature());
                    continue;
                }

                var voteEnvelopeCIP93Envelope = cip93EnvelopeE.get();

                var voteId = voteEnvelopeCIP93Envelope.getData().getId();
                var voteMerkleProof = VoteMerkleProof.builder()
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
            storeProofsStartStop.stop();

            log.info("Storing merkle proofs: {}, completed for event: {}, time: {} secs", l1MerkleCommitment.signedVotes().size(), l1MerkleCommitment.eventId(), storeProofsStartStop.getTotalTimeSeconds());
        }

        log.info("Storing vote merkle proofs for all events completed.");
    }

}
