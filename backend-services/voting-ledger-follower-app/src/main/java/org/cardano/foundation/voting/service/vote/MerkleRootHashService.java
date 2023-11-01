package org.cardano.foundation.voting.service.vote;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.IsMerkleRootPresentResult;
import org.cardano.foundation.voting.domain.entity.MerkleRootHash;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.List;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerkleRootHashService {

    private final MerkleRootHashRepository merkleRootHashRepository;

    private final ReferenceDataService referenceDataService;

    private final CardanoNetwork network;

    @Timed(value = "service.merkle_root.isPresent", histogram = true)
    public Either<Problem, IsMerkleRootPresentResult> isPresent(String event, String merkleRootHashHex) {
        var maybeEvent = referenceDataService.findEventByName(event);

        if (maybeEvent.isEmpty()) {
            log.info("No event in db found for {}", event);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withStatus(BAD_REQUEST)
                    .withDetail("No event in db found for:" + event)
                    .build());
        }

        return merkleRootHashRepository.findByEventIdAndId(event, merkleRootHashHex).map(merkleRootHash -> {
            return Either.<Problem, IsMerkleRootPresentResult>right(new IsMerkleRootPresentResult(true, network));
        }).orElse(Either.right(new IsMerkleRootPresentResult(false, network)));
    }

    @Timed(value = "service.merkle_root.storeCommitments", histogram = true)
    @Transactional
    public List<MerkleRootHash> storeCommitments(List<MerkleRootHash> merkleRootHashes) {
        log.info("Storing commitments:{}", merkleRootHashes);
        return merkleRootHashRepository.saveAllAndFlush(merkleRootHashes);
    }

}
