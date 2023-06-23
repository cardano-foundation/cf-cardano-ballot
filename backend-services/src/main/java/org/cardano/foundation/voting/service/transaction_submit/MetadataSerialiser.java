package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataList;
import com.bloxbean.cardano.client.util.HexUtil;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

import static org.cardano.foundation.voting.domain.EventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.EventType.USER_BASED;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(Event event) {
        var map = MetadataBuilder.createMap();

        map.put("type", "EventRegistration");

        map.put("name", event.getId());
        map.put("presentation_name", event.getPresentationName());
        map.put("team", event.getTeam());
        map.put("schema_version", event.getVersion().getSemVer());

        if (event.getEventType() == STAKE_BASED) {
            //noinspection ConstantConditions
            map.put("start_epoch", BigInteger.valueOf(event.getStartEpoch()));
            //noinspection ConstantConditions
            map.put("end_epoch", BigInteger.valueOf(event.getEndEpoch()));
            //noinspection ConstantConditions
            map.put("snapshot_epoch", BigInteger.valueOf(event.getSnapshotEpoch()));
        }
        if (event.getEventType() == USER_BASED) {
            //noinspection ConstantConditions
            map.put("start_slot", BigInteger.valueOf(event.getStartSlot()));
            //noinspection ConstantConditions
            map.put("end_slot", BigInteger.valueOf(event.getEndSlot()));
        }

        return map;
    }

    public MetadataMap serialise(Event event, Category category) {
        var map = MetadataBuilder.createMap();

        map.put("type", "CategoryRegistration");

        map.put("name", category.getId());
        map.put("event_id", category.getEvent().getId());
        map.put("presentation_name", category.getPresentationName());
        map.put("schema_version", category.getVersion().getSemVer());

        if (category.getProposals().isEmpty()) {
            throw new RuntimeException("Category " + category.getId() + " has no proposals!");
        }

        var proposalsList = MetadataBuilder.createList();

        for (var proposal : category.getProposals()) {
            var proposalMap = MetadataBuilder.createMap();

            if (event.isGdprProtection()) {
                proposalMap.put("id", proposal.getId());
            } else {
                proposalMap.put("id", proposal.getId());
                proposalMap.put("name", proposal.getProposalDetails().getName());
            }

            proposalsList.add(proposalMap);
        }

        map.put("proposals", (CBORMetadataList) proposalsList);

        return map;
    }

    public MetadataMap serialise(List<L1MerkleCommitment> l1MerkleCommitments) {
        var map = MetadataBuilder.createMap();

        map.put("type", "VotesCommitment");

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
