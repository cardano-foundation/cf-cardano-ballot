package org.cardano.foundation.voting.service.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.CoseWrappedVote;
import org.cardano.foundation.voting.domain.VoteVerificationRequest;
import org.cardano.foundation.voting.domain.VoteVerificationResult;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.MerkleTree;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static com.bloxbean.cardano.client.util.JsonUtil.parseJson;
import static org.cardano.foundation.voting.utils.VoteSerialisations.VOTE_SERIALISER;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteVerificationService {

    private final ChainFollowerClient chainFollowerClient;

    private final CardanoNetwork cardanoNetwork;

    @Timed(value = "service.verifyVote", histogram = true)
    public Either<Problem, VoteVerificationResult> verifyVoteProof(VoteVerificationRequest voteVerificationRequest) {
        var maybeSteps = voteVerificationRequest.getSteps();
        if (maybeSteps.isEmpty()) {
            log.warn("Merkle proof steps not found for:{}", voteVerificationRequest);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_MERKLE_PROOF")
                    .withStatus(BAD_REQUEST)
                    .withDetail("merkle proof steps not found!")
                    .build());
        }

        var cip30Parser = new CIP30Verifier(voteVerificationRequest.getVoteCoseSignature(), voteVerificationRequest.getVoteCosePublicKey());

        var cip30VerificationResult = cip30Parser.verify();

        if (!cip30VerificationResult.isValid()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VOTE")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Invalid vote's cose signature")
                    .build());
        }

        try {
            var jsonNode = parseJson(cip30VerificationResult.getMessage(TEXT));
            var dataNode = jsonNode.get("data");
            var event = dataNode.get("event").asText();

            var maybeEventE = chainFollowerClient.findEventById(event);

            if (maybeEventE.isEmpty()) {
                return Either.left(maybeEventE.getLeft());
            }

            var maybeEvent = maybeEventE.get();
            if (maybeEvent.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_EVENT")
                        .withDetail("Unsupported event: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            var e = maybeEvent.orElseThrow();
            if (e.notStarted()) {
                return Either.left(Problem.builder()
                        .withTitle("EVENT_NOT_STARTED")
                        .withDetail("Event not started yet: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            var voteNetwork = dataNode.get("network").asText();
            var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, voteNetwork);
            if (maybeNetwork.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("INVALID_NETWORK")
                        .withDetail("Invalid network.")
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            var network = maybeNetwork.orElseThrow();

            if (network != cardanoNetwork) {
                log.warn("Invalid network, network:{}", voteNetwork);

                return Either.left(Problem.builder()
                        .withTitle("NETWORK_MISMATCH")
                        .withDetail("Invalid network, backend configured with network:" + cardanoNetwork + ", however request is with network:" + network)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            var isPresent = chainFollowerClient.isMerkleProofPresent(event, voteVerificationRequest.getRootHash());

            if (!isPresent) {
                return Either.right(new VoteVerificationResult(false, network));
            }

            var steps = io.vavr.collection.List.ofAll(deserialiseProofItems(maybeSteps.orElseThrow()));

            var vote = new CoseWrappedVote(voteVerificationRequest.getVoteCoseSignature(), voteVerificationRequest.getVoteCosePublicKey());

            var rootHash = decodeHexString(voteVerificationRequest.getRootHash());
            var isVerified = MerkleTree.verifyProof(rootHash, vote, steps, VOTE_SERIALISER);

            return Either.right(new VoteVerificationResult(isVerified, network));
        } catch (JsonProcessingException e) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_JSON")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Invalid json in the vote!")
                    .build());
        }
    }

    public List<ProofItem> deserialiseProofItems(List<VoteVerificationRequest.MerkleProofItem> items) {
        return items.stream().map(item -> switch (item.getType()) {
            case L -> new ProofItem.Left(decodeHexString(item.getHash()));
            case R -> new ProofItem.Right(decodeHexString(item.getHash()));
        }).toList();
    }

}
