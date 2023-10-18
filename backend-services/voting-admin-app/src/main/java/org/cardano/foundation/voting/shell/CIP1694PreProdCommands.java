package org.cardano.foundation.voting.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.Proposal;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.CardanoNetwork.PREPROD;
import static org.cardano.foundation.voting.domain.SchemaVersion.V1;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;
import static org.cardano.foundation.voting.utils.MoreUUID.shortUUID;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CIP1694PreProdCommands {

    private final static String EVENT_NAME = "CIP-1694_Pre_Ratification";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cip-1694-event-pre-prod", value = "Create a CIP-1694 voting event on a PRE-PROD network.")
    public String createVoltairePreRatificationEvent() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD a network!";
        }

        log.info("Creating CIP-1694 event on PRE-PROD network...");

        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + shortUUID(4))
                .startEpoch(Optional.of(445))
                .endEpoch(Optional.of(445))
                .snapshotEpoch(Optional.of(443))
                .proposalsRevealEpoch(Optional.of(445))
                .votingPowerAsset(Optional.of(ADA))
                .organisers("IOG")
                .votingEventType(STAKE_BASED)
                .schemaVersion(V1)
                .allowVoteChanging(false)
                .highLevelEventResultsWhileVoting(false)
                .highLevelCategoryResultsWhileVoting(false)
                .categoryResultsWhileVoting(false)
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created CIP-1694 event: " + createEventCommand;
    }

    @ShellMethod(key = "02_create-cip-1694-approval-category-pre-prod", value = "Create a CIP1694_APPROVAL category on a PRE-PROD network.")
    public String createCIP1694ApprovalCategoryOnPreProd(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CIP-1694 CIP1694_APPROVAL Structure category...");

        Proposal yesProposal = Proposal.builder()
                .id("1f082124-ee46-4deb-9140-84a4529f98be")
                .name("YES")
                .build();

        Proposal noProposal = Proposal.builder()
                .id("ed9f03e8-8ee9-4de5-93a3-30779216f150")
                .name("NO")
                .build();

        Proposal abstainProposal = Proposal.builder()
                .id("fd9f03e8-8ee9-4de5-93a3-40779216f151")
                .name("NO")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("CIP1694_APPROVAL")
                .event(event)
                .gdprProtection(false)
                .schemaVersion(V1)
                .proposals(List.of(yesProposal, noProposal, abstainProposal))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CIP-1694 category: " + createCategoryCommand;
    }

}
