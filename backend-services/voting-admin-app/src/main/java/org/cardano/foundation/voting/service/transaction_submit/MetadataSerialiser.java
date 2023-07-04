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
import static org.cardano.foundation.voting.utils.MoreBoolean.toBigInteger;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(CreateEventCommand createEventCommand, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.EVENT_REGISTRATION.name());

        map.put("name", createEventCommand.getId());
        map.put("team", createEventCommand.getTeam());
        map.put("votingEventType", createEventCommand.getVotingEventType().name());
        map.put("schemaVersion", createEventCommand.getVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));

        if (createEventCommand.getVotingEventType() == STAKE_BASED) {
            //noinspection ConstantConditions
            map.put("startEpoch", BigInteger.valueOf(createEventCommand.getStartEpoch()));
            //noinspection ConstantConditions
            map.put("endEpoch", BigInteger.valueOf(createEventCommand.getEndEpoch()));
            //noinspection ConstantConditions
            map.put("snapshotEpoch", BigInteger.valueOf(createEventCommand.getSnapshotEpoch()));
        }
        if (createEventCommand.getVotingEventType() == USER_BASED) {
            //noinspection ConstantConditions
            map.put("startSlot", BigInteger.valueOf(createEventCommand.getStartSlot()));
            //noinspection ConstantConditions
            map.put("endSlot", BigInteger.valueOf(createEventCommand.getEndSlot()));
        }

        map.put("options", createEventOptions(createEventCommand));

        return map;
    }

    private static MetadataMap createEventOptions(CreateEventCommand createEventCommand) {
        var optionsMap = MetadataBuilder.createMap();
        optionsMap.put("allowVoteChanging", toBigInteger(createEventCommand.isAllowVoteChanging()));
        optionsMap.put("categoryResultsWhileVoting", toBigInteger(createEventCommand.isCategoryResultsWhileVoting()));

        return optionsMap;
    }

    public MetadataMap serialise(CreateCategoryCommand createCategoryCommand, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.CATEGORY_REGISTRATION.name());

        map.put("name", createCategoryCommand.getId());
        map.put("event", createCategoryCommand.getEvent());

        map.put("schemaVersion", createCategoryCommand.getSchemaVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));
        map.put("options", createCategoryOptions(createCategoryCommand));

        if (createCategoryCommand.getProposals().isEmpty()) {
            throw new RuntimeException("Category " + createCategoryCommand.getId() + " has no proposals!");
        }

        var proposalsList = MetadataBuilder.createList();

        for (var proposal : createCategoryCommand.getProposals()) {
            var proposalMap = MetadataBuilder.createMap();

            if (createCategoryCommand.isGdprProtection()) {
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

    private static MetadataMap createCategoryOptions(CreateCategoryCommand createCategoryCommand) {
        var optionsMap = MetadataBuilder.createMap();
        optionsMap.put("gdprProtection", toBigInteger(createCategoryCommand.isGdprProtection()));

        return optionsMap;
    }

}
