package org.cardano.foundation.voting.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerkleRootHashClient {

    private final RestTemplate restTemplate;

    @Value("$ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public boolean isPresent(String eventId, String merkleRootHash) {
        log.info("Checking if merkle root hash is present in ledger follower: eventId={}, merkleRootHash={}", eventId, merkleRootHash);

        var url = ledgerFollowerBaseUrl + "/api/merkle-root-hash/{eventId}/{merkleRootHash}";

        var merkleRootHashResponse = restTemplate.getForObject(url, MerkleRootHashResponse.class, eventId, merkleRootHash);
        log.info("Merkle root hash: {}, response: {}", merkleRootHash, merkleRootHashResponse);

        return Optional.ofNullable(merkleRootHashResponse).map(MerkleRootHashResponse::isPresent).orElse(false);
    }

    record MerkleRootHashResponse(boolean isPresent) { }

}
