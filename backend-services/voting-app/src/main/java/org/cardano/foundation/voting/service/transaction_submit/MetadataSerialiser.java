package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.util.HexUtil;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(List<L1MerkleCommitment> l1MerkleCommitments, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.COMMITMENTS.name());
        map.put("schemaVersion", SchemaVersion.V1.getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));

        boolean addedAnyValues = false;

        var l1CommitmentMap = MetadataBuilder.createMap();
        for (var l1MerkleCommitment : l1MerkleCommitments) {

            if (l1MerkleCommitment.votes().isEmpty()) {
                continue;
            }

            var l1Map = MetadataBuilder.createMap();
            l1Map.put("hash", HexUtil.encodeHexString(l1MerkleCommitment.root().itemHash()));

            l1CommitmentMap.put(l1MerkleCommitment.event().getId(), l1Map);
        }

        map.put("commitments", l1CommitmentMap);

        return map;
    }

}
