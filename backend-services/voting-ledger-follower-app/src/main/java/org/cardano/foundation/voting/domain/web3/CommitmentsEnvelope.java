package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.OnChainEventType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class CommitmentsEnvelope {

    private OnChainEventType type;
    private String schemaVersion;
    private long creationSlot;

    // event -> commitment map (hash) and
    @Builder.Default
    private Map<String, Map<String, String>> commitments = new LinkedHashMap<>();

    public void addCommitment(String eventId, String rootHash) {
        commitments.put(eventId, Map.of("hash", rootHash));
    }

    public Optional<String> getCommitment(String eventId) {
        return Optional.ofNullable(commitments.get(eventId))
                .flatMap(eId -> Optional.ofNullable(eId.get("hash")));
    }

}
