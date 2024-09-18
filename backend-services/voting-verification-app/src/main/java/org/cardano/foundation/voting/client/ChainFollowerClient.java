package org.cardano.foundation.voting.client;


import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChainFollowerClient {

    private final RestTemplate restTemplate;

    private final Network network;

    @Value("${ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public boolean isMerkleProofPresent(String eventId, String merkleRootHash) {
        log.info("Checking if merkle root hash is present in ledger follower: eventId={}, merkleRootHash={}", eventId, merkleRootHash);

        var url = String.format("%s/api/merkle-root-hash/{eventId}/{merkleRootHash}", ledgerFollowerBaseUrl);

        var merkleRootHashResponse = restTemplate.getForObject(url, MerkleRootHashResponse.class, eventId, merkleRootHash);
        log.info("Merkle root hash: {}, response: {}", merkleRootHash, merkleRootHashResponse);

        return Optional.ofNullable(merkleRootHashResponse)
                .map(mrh -> {
                    if (network != mrh.network()) {
                        log.warn("Network mismatch: expected={}, actual={}", network, mrh.network());

                        return false;
                    }

                    return mrh.isPresent();
                }).orElse(false);
    }

    public Either<Problem, Optional<EventSummary>> findEventById(String eventId) {
        return findAllEvents()
                .map(eventSummaries -> eventSummaries.stream()
                        .filter(event -> event.id().equals(eventId)).findFirst());
    }

    public Either<Problem, List<EventSummary>> findAllEvents() {
        var url = String.format("%s/api/reference/event", ledgerFollowerBaseUrl);

        try {
            var allEventSummaries = Optional.ofNullable(restTemplate.getForObject(url, EventSummary[].class))
                    .map(Arrays::asList).orElse(List.of());

            return Either.right(allEventSummaries);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(List.of());
            }

            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get event details from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public record EventSummary(String id,
                               boolean finished,
                               boolean notStarted,
                               boolean active) {

        public boolean isEventInactive() {
            return !active;
        }

    }

    record MerkleRootHashResponse(boolean isPresent, Network network) { }

}
