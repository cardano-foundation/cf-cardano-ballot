package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.util.HexUtil;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(List<L1MerkleCommitment> l1MerkleCommitments) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.COMMITMENTS.name());

        for (var l1MerkleCommitment : l1MerkleCommitments) {
            var l1CommitmentMap = MetadataBuilder.createMap();

            var l1Map = MetadataBuilder.createMap();
            l1Map.put("hash", HexUtil.encodeHexString(l1MerkleCommitment.root().itemHash()));

            l1CommitmentMap.put(l1MerkleCommitment.event().getId(), l1Map);

            map.put("commitments", l1CommitmentMap);
        }

        return map;
    }

}
