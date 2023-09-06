package org.cardano.foundation.voting.service.vote;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.IsMerkleRootPresentResult;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerkleRootHashService {

    private final MerkleRootHashRepository merkleRootHashRepository;

    private final ReferenceDataService referenceDataService;

    private final CardanoNetwork network;

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

}
