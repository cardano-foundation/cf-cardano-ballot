package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataList;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

import static org.cardano.foundation.voting.domain.OnChainEventType.EVENT_REGISTRATION;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.utils.MoreBoolean.toBigInteger;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(CreateEventCommand createEventCommand, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", EVENT_REGISTRATION.name());

        map.put("id", createEventCommand.getId());
        map.put("organisers", createEventCommand.getOrganisers());
        map.put("votingEventType", createEventCommand.getVotingEventType().name());
        map.put("schemaVersion", createEventCommand.getSchemaVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));
        map.put("publicKey", "3op4jfpo3j434i39845");

        if (List.of(STAKE_BASED, BALANCE_BASED).contains(createEventCommand.getVotingEventType())) {
            map.put("startEpoch", BigInteger.valueOf(createEventCommand.getStartEpoch().orElseThrow()));
            map.put("endEpoch", BigInteger.valueOf(createEventCommand.getEndEpoch().orElseThrow()));
            map.put("snapshotEpoch", BigInteger.valueOf(createEventCommand.getSnapshotEpoch().orElseThrow()));
            map.put("votingPowerAsset", createEventCommand.getVotingPowerAsset().orElseThrow().name());
            map.put("proposalsRevealEpoch", BigInteger.valueOf(createEventCommand.getProposalsRevealEpoch().orElseThrow()));
        }
        if (createEventCommand.getVotingEventType() == USER_BASED) {
            map.put("startSlot", BigInteger.valueOf(createEventCommand.getStartSlot().orElseThrow()));
            map.put("endSlot", BigInteger.valueOf(createEventCommand.getEndSlot().orElseThrow()));
            map.put("proposalsRevealSlot", BigInteger.valueOf(createEventCommand.getProposalsRevealSlot().orElseThrow()));
        }

        map.put("options", createEventOptions(createEventCommand));

        return map;
    }

    private static MetadataMap createEventOptions(CreateEventCommand createEventCommand) {
        var optionsMap = MetadataBuilder.createMap();
        optionsMap.put("allowVoteChanging", toBigInteger(createEventCommand.isAllowVoteChanging()));

        optionsMap.put("highLevelEventResultsWhileVoting", toBigInteger(createEventCommand.isHighLevelEventResultsWhileVoting()));
        optionsMap.put("highLevelCategoryResultsWhileVoting", toBigInteger(createEventCommand.isHighLevelCategoryResultsWhileVoting()));
        optionsMap.put("categoryResultsWhileVoting", toBigInteger(createEventCommand.isCategoryResultsWhileVoting()));

        return optionsMap;
    }

    public MetadataMap serialise(CreateCategoryCommand createCategoryCommand, long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", OnChainEventType.CATEGORY_REGISTRATION.name());

        map.put("id", createCategoryCommand.getId());
        map.put("event", createCategoryCommand.getEvent());

        map.put("schemaVersion", createCategoryCommand.getSchemaVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));
        map.put("options", createCategoryOptions(createCategoryCommand));

        if (createCategoryCommand.getProposals().isEmpty()) {
            throw new RuntimeException("Category " + createCategoryCommand.getId() + " has no proposals!");
        }

        map.put("proposals", createProposals(createCategoryCommand));

        return map;
    }

    private static MetadataList createProposals(CreateCategoryCommand createCategoryCommand) {
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
        return proposalsList;
    }

    private static MetadataMap createCategoryOptions(CreateCategoryCommand createCategoryCommand) {
        var optionsMap = MetadataBuilder.createMap();
        optionsMap.put("gdprProtection", toBigInteger(createCategoryCommand.isGdprProtection()));

        return optionsMap;
    }

}
