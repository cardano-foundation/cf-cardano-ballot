package org.cardano.foundation.voting.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.CardanoNetwork.MAIN;
import static org.cardano.foundation.voting.domain.SchemaVersion.V1;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CIP1694ProductionCommands {

    private final static String EVENT_NAME = "CIP-1694_Pre_Ratification";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cip-1694-event-mainnet", value = "Create a CIP-1694 voting event on a MAINNET network.")
    public String createVoltairePreRatificationEvent() {
        if (network != MAIN) {
            return "This command can only be run on MAINNET a network!";
        }

        log.info("Creating CIP-1694 event on MAINNET network...");

//        Last day for users to stake their ada - 21 Nov - Last day of Epoch 449

//        Snapshot  - 21 Nov- End of Epoch 449 - Before 21:44 UTC
//        Poll Starts  - 01 Dec - First day of Epoch 452 at 21:45 UTC
//        Poll Ends - 11 Dec - Last day of Epoch 453 at 21:44 UTC
//        Poll Results/Reveal date - 16 Dec - First day of Epoch 455 at 21:45 UTC

        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME)
                .startEpoch(Optional.of(452))
                .endEpoch(Optional.of(453))
                .snapshotEpoch(Optional.of(449))
                .proposalsRevealEpoch(Optional.of(455))
                .votingPowerAsset(Optional.of(ADA))
                .organisers("IOG with CF Technical Support")
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

    @ShellMethod(key = "02_create-cip-1694-approval-category-mainnet", value = "Create a CIP1694_APPROVAL category on a MAINNET network.")
    public String createCIP1694ApprovalCategoryOnMainnet(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAINNET network!";
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
                .name("ABSTAIN")
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

    @ShellMethod(key = "03_submit-cip-1694-tally-results-mainnet", value = "Submit CIP-1694_Pre_Ratification tally results on the MAINNET network.")
    public String submitCIP1694TallyResultsOnMainnet() {
        if (network != MAIN) {
            return "This command can only be run on a MAINNET network!";
        }

        log.info("Creating CIP-1694 CIP1694_APPROVAL Structure category...");

        ProposalResult yesProposal = ProposalResult.builder()
                .id("1f082124-ee46-4deb-9140-84a4529f98be")
                .name("YES")
                .voteCount("720")
                .votingPower("110744335923027")
                .build();

        ProposalResult noProposal = ProposalResult.builder()
                .id("ed9f03e8-8ee9-4de5-93a3-30779216f150")
                .name("NO")
                .voteCount("30")
                .votingPower("148003932083")
                .build();

        ProposalResult abstainProposal = ProposalResult.builder()
                .id("fd9f03e8-8ee9-4de5-93a3-40779216f151")
                .name("ABSTAIN")
                .voteCount("21")
                .votingPower("835765197287")
                .build();

        CreateTallyResultCommand createTallyResultCommand = CreateTallyResultCommand.builder()
                .id("CIP-1694_Pre_Ratification")
                .gdprProtection(false)
                .showVoteCount(true)
                .categoryResults(List.of(CategoryResult.builder()
                        .id("CIP1694_APPROVAL")
                        .proposalResults(List.of(yesProposal, noProposal, abstainProposal))
                        .build()))
                .build();

        l1SubmissionService.submitTallyResults(createTallyResultCommand);

        return "Submitted CIP-1694 tally results: " + createTallyResultCommand;
    }

}
