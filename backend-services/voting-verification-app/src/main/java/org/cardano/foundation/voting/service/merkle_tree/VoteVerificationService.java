package org.cardano.foundation.voting.service.merkle_tree;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.domain.VoteVerificationRequest;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardanofoundation.merkle.MerkleTree;
import org.cardanofoundation.merkle.ProofItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.domain.Vote.VOTE_SERIALISER;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class VoteVerificationService {

    @Autowired
    private MerkleRootHashRepository merkleRootHashRepository;

    @Autowired
    private ReferenceDataService referenceDataService;

    public Either<Problem, Boolean> verifyVoteProof(VoteVerificationRequest voteVerificationRequest) {
        var maybeRoot = merkleRootHashRepository.findById(voteVerificationRequest.getRootHash());

        if (maybeRoot.isEmpty()) {
            log.info("No root hash in db found for {}", voteVerificationRequest.getRootHash());

            return Either.right(false);
        }

        var root = maybeRoot.get();
        var event = root.getEventId();

        var maybeEvent = referenceDataService.findEventByName(event);
        if (maybeEvent.isEmpty()) {
            log.info("No event in db found for {}", event);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withStatus(BAD_REQUEST)
                    .withDetail("No event in db found for:" + event)
                    .build());
        }

        var maybeSteps = voteVerificationRequest.getSteps();
        if (maybeSteps.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_MERKLE_PROOF")
                    .withStatus(BAD_REQUEST)
                    .withDetail("merkle proof steps not found!")
                    .build());
        }

        var steps = io.vavr.collection.List.ofAll(deserialiseProofItems(maybeSteps.orElseThrow()));

        var vote = new Vote(voteVerificationRequest.getVoteCoseSignature(), voteVerificationRequest.getVoteCosePublicKey());

        byte[] rootHash = decodeHexString(root.getMerkleRootHash());
        boolean isVerified = MerkleTree.verifyProof(rootHash, vote, steps, VOTE_SERIALISER);

        return Either.right(isVerified);
    }

    public List<ProofItem> deserialiseProofItems(List<VoteVerificationRequest.MerkleProofItem> items) {
        return items.stream().map(item -> switch (item.getType()) {
            case Left -> new ProofItem.Left(decodeHexString(item.getHash()));
            case Right -> new ProofItem.Right(decodeHexString(item.getHash()));
        }).toList();
    }

}
