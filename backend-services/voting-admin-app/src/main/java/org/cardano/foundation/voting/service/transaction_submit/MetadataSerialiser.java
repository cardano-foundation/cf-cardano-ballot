package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(CreateEventCommand event, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.EVENT_REGISTRATION.name());

        map.put("name", event.getId());
        map.put("team", event.getTeam());
        map.put("votingEventType", event.getVotingEventType().name());
        map.put("schemaVersion", event.getVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));

        if (event.getVotingEventType() == STAKE_BASED) {
            //noinspection ConstantConditions
            map.put("startEpoch", BigInteger.valueOf(event.getStartEpoch()));
            //noinspection ConstantConditions
            map.put("endEpoch", BigInteger.valueOf(event.getEndEpoch()));
            //noinspection ConstantConditions
            map.put("snapshotEpoch", BigInteger.valueOf(event.getSnapshotEpoch()));
        }
        if (event.getVotingEventType() == USER_BASED) {
            //noinspection ConstantConditions
            map.put("startSlot", BigInteger.valueOf(event.getStartSlot()));
            //noinspection ConstantConditions
            map.put("endSlot", BigInteger.valueOf(event.getEndSlot()));
        }

        return map;
    }

    public MetadataMap serialise(CreateCategoryCommand category, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.CATEGORY_REGISTRATION.name());

        map.put("name", category.getId());
        map.put("event", category.getEvent());
        map.put("gdprProtection", Boolean.toString(category.isGdprProtection()));
        map.put("schemaVersion", category.getSchemaVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));

        if (category.getProposals().isEmpty()) {
            throw new RuntimeException("Category " + category.getId() + " has no proposals!");
        }

        var proposalsList = MetadataBuilder.createList();

        for (var proposal : category.getProposals()) {
            var proposalMap = MetadataBuilder.createMap();

            if (category.isGdprProtection()) {
                proposalMap.put("id", proposal.getId());
            } else {
                proposalMap.put("id", proposal.getId());
                proposalMap.put("name", proposal.getName());
            }

            proposalsList.add(proposalMap);
        }

        map.put("proposals", proposalsList);

        return map;
    }

}
