package org.cardano.foundation.voting.shell;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;
import static org.cardano.foundation.voting.utils.MoreUUID.shortUUID;

@ShellComponent
@Slf4j
public class CFVotingAdminCommands {

    private final static String EVENT_NAME = "CIP-1694_Pre_Ratification";

    @Autowired
    private L1SubmissionService l1SubmissionService;

    @ShellMethod(key = "create-cip-1694-event", value = "Create a CIP-1694 voting event")
    public String createVoltairePreRatificationEvent() {
        log.info("Creating CIP-1694 event...");

        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + shortUUID(4))
                .startEpoch(Optional.of(80))
                .endEpoch(Optional.of(95))
                .snapshotEpoch(Optional.of(79))
                .votingPowerAsset(Optional.of(ADA))
                .team("CF & IOG")
                .votingEventType(VotingEventType.STAKE_BASED)
                .version(SchemaVersion.V1)
                .allowVoteChanging(false)
                .categoryResultsWhileVoting(false)
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created CIP-1694 event: " + createEventCommand;
    }

    @ShellMethod(key = "create-cip-1694-category", value = "Create a CIP-1694 category")
    public String createVoltairePreRatificationCategory(@ShellOption String event) {
        log.info("Creating CIP-1694 category...");

        Proposal yesProposal = Proposal.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name("YES")
                .build();

        Proposal noProposal = Proposal.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name("NO")
                .build();

        Proposal abstainProposal = Proposal.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name("ABSTAIN")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id(String.format("%s_%s", EVENT_NAME, event.substring(event.length() - 4)))
                .event(event)
                .gdprProtection(false)
                .schemaVersion(SchemaVersion.V1)
                .proposals(List.of(yesProposal, noProposal, abstainProposal))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CIP-1694 category: " + createCategoryCommand;
    }

}
