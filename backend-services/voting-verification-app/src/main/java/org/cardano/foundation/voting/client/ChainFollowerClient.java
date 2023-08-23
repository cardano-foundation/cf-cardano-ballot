package org.cardano.foundation.voting.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChainFollowerClient {

    private final RestTemplate restTemplate;

    private final CardanoNetwork network;

    @Value("${ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public boolean isMerkleProofPresent(String eventId, String merkleRootHash) {
        log.info("Checking if merkle root hash is present in ledger follower: eventId={}, merkleRootHash={}", eventId, merkleRootHash);

        var url = String.format("%s/api/merkle-root-hash/{eventId}/{merkleRootHash}", ledgerFollowerBaseUrl);

        var merkleRootHashResponse = restTemplate.getForObject(url, MerkleRootHashResponse.class, eventId, merkleRootHash);
        log.info("Merkle root hash: {}, response: {}", merkleRootHash, merkleRootHashResponse);


        return Optional.ofNullable(merkleRootHashResponse)
                .map(r -> {
                    if (network != r.network()) {
                        log.warn("Network mismatch: expected={}, actual={}", network, r.network());

                        return false;
                    }

                    return r.isPresent();
                }).orElse(false);
    }

    record MerkleRootHashResponse(boolean isPresent, CardanoNetwork network) { }

}
