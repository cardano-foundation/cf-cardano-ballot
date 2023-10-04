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
public class FruitsPreProdCommands {

    private final static String EVENT_NAME = "FRUITS";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_fruits-event-pre-prod", value = "Create a Fruits voting event on a PRE-PROD network.")
    public String createFruitsEvent() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD a network!";
        }

        log.info("Creating FRUITS event on PRE-PROD network...");

        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + shortUUID(4))
                .startEpoch(Optional.of(96))
                .endEpoch(Optional.of(102))
                .snapshotEpoch(Optional.of(96))
                .proposalsRevealEpoch(Optional.of(103))
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

        return "Created FRUITS event: " + createEventCommand;
    }

    @ShellMethod(key = "02_create-fruits-pizza-pre-prod", value = "Create a FRUITS Pizza category on a PRE-PROD network.")
    public String createPizzaOnPreProd(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating Pizza category...");

        Proposal yesProposal = Proposal.builder()
                .id("a44c8150-c5ee-4e78-bd66-09cafb1eeccc")
                .name("YES")
                .build();

        Proposal noProposal = Proposal.builder()
                .id("6c524586-0b5a-403f-a6be-d65df3053582")
                .name("NO")
                .build();

        Proposal abstainProposal = Proposal.builder()
                .id("72c9c8dc-1187-48ae-a8d4-7f302f953d48")
                .name("ABSTAIN")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("PIZZA")
                .event(event)
                .gdprProtection(false)
                .schemaVersion(V1)
                .proposals(List.of(yesProposal, noProposal, abstainProposal))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created FRUITS category: " + createCategoryCommand;
    }

    @ShellMethod(key = "03_create-fruits-apples-pre-prod", value = "Create a FRUITS Apples category on a PRE-PROD network.")
    public String createFruitsApplesCategoryOnPreProd(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating FRUITS Apples category...");

        Proposal yesProposal = Proposal.builder()
                .id("268d9607-9ca3-4f8a-af56-3a866b04c2f6")
                .name("YES")
                .build();

        Proposal noProposal = Proposal.builder()
                .id("a0d60991-10b1-400a-9f3e-a2d27728e340")
                .name("NO")
                .build();

        Proposal abstainProposal = Proposal.builder()
                .id("03c20667-3b80-4c6a-a2ee-ada41e53a140")
                .name("ABSTAIN")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("APPLES")
                .event(event)
                .gdprProtection(false)
                .schemaVersion(V1)
                .proposals(List.of(yesProposal, noProposal, abstainProposal))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created FRUITS category: " + createCategoryCommand;
    }

}
