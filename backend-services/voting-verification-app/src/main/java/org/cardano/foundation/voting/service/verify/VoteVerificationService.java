package org.cardano.foundation.voting.service.verify;

import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.KeriVerificationClient;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.merkle.MerkleTree;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

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
    private final KeriVerificationClient keriVerificationClient;

    private final Network network;

    @Timed(value = "service.verifyVote", histogram = true)
    public Either<Problem, VoteVerificationResult> verifyVoteProof(VoteVerificationRequest voteVerificationRequest) {
        val maybeSteps = voteVerificationRequest.getSteps();
        if (maybeSteps.isEmpty()) {
            log.warn("Merkle proof steps not found for:{}", voteVerificationRequest);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_MERKLE_PROOF")
                    .withStatus(BAD_REQUEST)
                    .withDetail("merkle proof steps not found!")
                    .build());
        }

        return switch (voteVerificationRequest.getWalletType()) {
            case WalletType.CARDANO -> verifyForCardano(voteVerificationRequest, maybeSteps);
            case WalletType.KERI -> verifyForKeri(voteVerificationRequest, maybeSteps);
        };
    }

    private Either<Problem, VoteVerificationResult> verifyForCardano(VoteVerificationRequest voteVerificationRequest,
                                                                     Optional<List<VoteVerificationRequest.MerkleProofItem>> stepsM) {
        val cip30Parser = new CIP30Verifier(voteVerificationRequest.getSignature(), voteVerificationRequest.getPublicKey());

        val cip30VerificationResult = cip30Parser.verify();

        if (!cip30VerificationResult.isValid()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VOTE")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Invalid vote's cose signature")
                    .build());
        }

        try {
            val jsonNode = parseJson(cip30VerificationResult.getMessage(TEXT));
            val dataNode = jsonNode.get("data");
            val event = dataNode.get("event").asText();

            val maybeEventE = chainFollowerClient.findEventById(event);

            if (maybeEventE.isEmpty()) {
                return Either.left(maybeEventE.getLeft());
            }

            val maybeEvent = maybeEventE.get();
            if (maybeEvent.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_EVENT")
                        .withDetail("Unsupported event: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val e = maybeEvent.orElseThrow();
            if (e.notStarted()) {
                return Either.left(Problem.builder()
                        .withTitle("EVENT_NOT_STARTED")
                        .withDetail("Event not started yet: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val voteNetwork = dataNode.get("network").asText();
            val maybeNetwork = Enums.getIfPresent(Network.class, voteNetwork);
            if (maybeNetwork.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("INVALID_NETWORK")
                        .withDetail("Invalid network.")
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            val network = maybeNetwork.orElseThrow();

            if (network != network) {
                log.warn("Invalid network, network:{}", voteNetwork);

                return Either.left(Problem.builder()
                        .withTitle("NETWORK_MISMATCH")
                        .withDetail("Invalid network, backend configured with network:" + network + ", however request is with network:" + network)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val isPresent = chainFollowerClient.isMerkleProofPresent(event, voteVerificationRequest.getRootHash());

            if (!isPresent) {
                return Either.right(new VoteVerificationResult(false, WalletType.CARDANO, network));
            }

            val steps = io.vavr.collection.List.ofAll(deserialiseProofItems(stepsM.orElseThrow()));

            val vote = WrappedVote.createCardanoVote(
                    voteVerificationRequest.getWalletId(),
                    voteVerificationRequest.getSignature(),
                    voteVerificationRequest.getPublicKey()
            );

            val rootHash = decodeHexString(voteVerificationRequest.getRootHash());
            val isVerified = MerkleTree.verifyProof(rootHash, vote, steps, VOTE_SERIALISER);

            return Either.right(new VoteVerificationResult(isVerified, WalletType.CARDANO, network));
        } catch (JsonProcessingException e) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_JSON")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Invalid json in the vote!")
                    .build());
        }
    }

    private Either<Problem, VoteVerificationResult> verifyForKeri(VoteVerificationRequest voteVerificationRequest,
                                                                  Optional<List<VoteVerificationRequest.MerkleProofItem>> stepsM) {
        val walletId = voteVerificationRequest.getWalletId();
        val walletType = voteVerificationRequest.getWalletType();
        val signature = voteVerificationRequest.getSignature();
        val payloadM = voteVerificationRequest.getPayload();

        if (payloadM.isEmpty()) {
            log.warn("Payload not found for:{}", voteVerificationRequest);

            return Either.left(Problem.builder()
                    .withTitle("INVALID_PAYLOAD")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Payload not found!")
                    .build());
        }

        val payload = voteVerificationRequest.getPayload().orElseThrow();

        val keriVerificationResultE = keriVerificationClient.verifySignature(walletId, signature, payload);
        if (keriVerificationResultE.isLeft()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VOTE")
                    .withStatus(BAD_REQUEST)
                    .withDetail("Invalid KERI signature")
                    .build());
        }

        try {
            val jsonNode = parseJson(new String(HexUtil.decodeHexString(payload)));
            val dataNode = jsonNode.get("data");
            val event = dataNode.get("event").asText();

            val maybeEventE = chainFollowerClient.findEventById(event);

            if (maybeEventE.isEmpty()) {
                return Either.left(maybeEventE.getLeft());
            }

            val maybeEvent = maybeEventE.get();
            if (maybeEvent.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_EVENT")
                        .withDetail("Unsupported event: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val e = maybeEvent.orElseThrow();
            if (e.notStarted()) {
                return Either.left(Problem.builder()
                        .withTitle("EVENT_NOT_STARTED")
                        .withDetail("Event not started yet: " + event)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val voteNetwork = dataNode.get("network").asText();
            val maybeNetwork = Enums.getIfPresent(Network.class, voteNetwork);
            if (maybeNetwork.isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("INVALID_NETWORK")
                        .withDetail("Invalid network.")
                        .withStatus(BAD_REQUEST)
                        .build());
            }
            val network = maybeNetwork.orElseThrow();

            if (network != network) {
                log.warn("Invalid network, network:{}", voteNetwork);

                return Either.left(Problem.builder()
                        .withTitle("NETWORK_MISMATCH")
                        .withDetail("Invalid network, backend configured with network:" + network + ", however request is with network:" + network)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            val isPresent = chainFollowerClient.isMerkleProofPresent(event, voteVerificationRequest.getRootHash());

            if (!isPresent) {
                return Either.right(new VoteVerificationResult(false, WalletType.KERI, network));
            }

            val steps = io.vavr.collection.List.ofAll(deserialiseProofItems(stepsM.orElseThrow()));

            val vote = WrappedVote.createKERIVote(
                    voteVerificationRequest.getWalletId(),
                    voteVerificationRequest.getSignature(),
                    payload
            );

            val rootHash = decodeHexString(voteVerificationRequest.getRootHash());
            val isVerified = MerkleTree.verifyProof(rootHash, vote, steps, VOTE_SERIALISER);

            return Either.right(new VoteVerificationResult(isVerified, WalletType.KERI, network));
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
