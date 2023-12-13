package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataList;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import org.cardano.foundation.voting.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

import static org.cardano.foundation.voting.domain.OnChainEventType.EVENT_REGISTRATION;
import static org.cardano.foundation.voting.domain.TallyType.HYDRA;
import static org.cardano.foundation.voting.domain.VotingEventType.*;
import static org.cardano.foundation.voting.utils.MoreBoolean.toBigInteger;

@Service
public class MetadataSerialiser {

    public MetadataMap serialise(CreateEventCommand createEventCommand,
                                 long slot) {
        var map = MetadataBuilder.createMap();

        map.put("type", EVENT_REGISTRATION.name());

        map.put("id", createEventCommand.getId());
        map.put("organisers", createEventCommand.getOrganisers());
        map.put("votingEventType", createEventCommand.getVotingEventType().name());
        map.put("schemaVersion", createEventCommand.getSchemaVersion().getSemVer());
        map.put("creationSlot", BigInteger.valueOf(slot));
        map.put("votesHashAlgo", "BLAKE2b-256");

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

        List<TallyCommand> tallies = createEventCommand.getTallies();

        var tallyList = MetadataBuilder.createList();

        for (var tally : tallies) {
            var tallyBag = MetadataBuilder.createMap();

            tallyBag.put("name", tally.getName());
            tallyBag.put("desc", tally.getDescription());
            tallyBag.put("type", tally.getType().name().toUpperCase());
            tallyBag.put("mode", tally.getMode().name().toUpperCase());
            tallyBag.put("partiesCount", BigInteger.valueOf(tally.getIndependentPartiesCount()));

            if (tally.getType() == HYDRA) {
                tallyBag.put("config", createHydraTallyConfig(tally));
            }

            tallyList.add(tallyBag);
        }

        map.put("tallies", tallyList);

        map.put("options", createEventOptions(createEventCommand));

        return map;
    }

    private static MetadataMap createHydraTallyConfig(TallyCommand tally) {
        var tallyHydra = (HydraTallyConfig) tally.getConfig();

        var tallyHydraConfig = MetadataBuilder.createMap();
        tallyHydraConfig.put("contractName", tallyHydra.getContractName());
        tallyHydraConfig.put("contractDesc", tallyHydra.getContractDescription());
        tallyHydraConfig.put("contractVersion", tallyHydra.getContractVersion());

        tallyHydraConfig.put("compiledScript", tallyHydra.getCompiledScript());
        tallyHydraConfig.put("compiledScriptHash", tallyHydra.getCompiledScriptHash());

        tallyHydraConfig.put("compilerName", tallyHydra.getCompilerName());
        tallyHydraConfig.put("compilerVersion", tallyHydra.getCompilerVersion());
        tallyHydraConfig.put("plutusVersion", tallyHydra.getPlutusVersion());

        var verificationKeys = MetadataBuilder.createList();
        tallyHydra.getPartiesVerificationKeys().forEach(verificationKeys::add);

        tallyHydraConfig.put("verificationKeys", verificationKeys);

        return tallyHydraConfig;
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

    public MetadataMap serialise(CreateTallyResultCommand createTallyResultCommand) {
        var categoryResults = MetadataBuilder.createList();
        var tallyResultsMap = MetadataBuilder.createMap();
        tallyResultsMap.put("id", createTallyResultCommand.getId());
        tallyResultsMap.put("categories", categoryResults);

        for (CategoryResult category : createTallyResultCommand.getCategoryResults()) {
            var proposalResults = MetadataBuilder.createList();
            var categoryResult = MetadataBuilder.createMap();
            categoryResults.add(categoryResult);
            categoryResult.put("id", category.getId());
            categoryResult.put("proposals", proposalResults);

            for (ProposalResult proposal : category.getProposalResults()) {
                var proposalResultsMap = getProposalResultsMap(createTallyResultCommand, proposal);
                proposalResults.add(proposalResultsMap);
            }

        }

        return tallyResultsMap;
    }

    private static MetadataMap getProposalResultsMap(CreateTallyResultCommand createTallyResultCommand, ProposalResult proposal) {
        var proposalResultsMap = MetadataBuilder.createMap();
        proposalResultsMap.put("id", proposal.getId());
        if (!createTallyResultCommand.isGdprProtection()) {
            proposalResultsMap.put("name", proposal.getName());
        }
        if (createTallyResultCommand.isShowVoteCount()) {
            proposalResultsMap.put("voteCount", proposal.getVoteCount());
        }
        proposalResultsMap.put("votingPower", proposal.getVotingPower());
        return proposalResultsMap;
    }

    // Create Tally Results
    // Create Domain Class TallyResults
    // CreateTallyResultCommand
    // event (id) > [category] (id) > [proposal] (only proposal_id)

// categories: [
//    proposals": [
//    {
//        "id": "1f082124-ee46-4deb-9140-84a4529f98be",
//            "name": "YES"
//    "voteCount": 123, // Confirm w/ Valeria
//    "votingPower": "lovelaces" // use as a string.
//    },
//    {

//    {
//    "id": "CIP-1694_Pre_Ratification",
//    "organisers": "IOG with CF Technical Support",
//    "votingEventType": "STAKE_BASED",
//    "startSlot": null,
//    "endSlot": null,
//    "proposalsRevealSlot": null,
//    "startEpoch": 452,
//    "eventStartDate": "2023-12-01T21:44:51Z",
//    "eventEndDate": "2023-12-11T21:44:50Z",
//    "proposalsRevealDate": "2023-12-16T21:44:51Z",
//    "snapshotTime": "2023-11-21T21:44:50Z",
//    "endEpoch": 453,
//    "snapshotEpoch": 449,
//    "proposalsRevealEpoch": 455,
//    "categories": [
//        {
//            "id": "CIP1694_APPROVAL",
//            "gdprProtection": false,
//            "proposals": [
//                {
//                    "id": "1f082124-ee46-4deb-9140-84a4529f98be",
//                    "name": "YES"
//                },
//                {
//                    "id": "ed9f03e8-8ee9-4de5-93a3-30779216f150",
//                    "name": "NO"
//                },
//                {
//                    "id": "fd9f03e8-8ee9-4de5-93a3-40779216f151",
//                    "name": "ABSTAIN"
//                }
//            ]
//        }
//    ],
//    "active": false,
//    "started": true,
//    "highLevelEventResultsWhileVoting": false,
//    "highLevelCategoryResultsWhileVoting": false,
//    "categoryResultsWhileVoting": false,
//    "notStarted": false,
//    "finished": true,
//    "proposalsReveal": false,
//    "commitmentsWindowOpen": true,
//    "allowVoteChanging": false
//}


}
