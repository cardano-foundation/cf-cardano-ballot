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
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;
import static org.cardano.foundation.voting.utils.MoreUUID.shortUUID;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CardanoSummit2023PreProdCommands {

    private final static String EVENT_NAME = "CF_SUMMIT_2023";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "create-cf-summit03-event-pre-prod", value = "Create a CF-Summit 2023 voting event on a PRE-PROD network.")
    public String createCFSummit2023Event() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2023 on a PRE-PROD network...");

        long startSlot = 37192182L;
        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + shortUUID(4))
                .startSlot(Optional.of(startSlot))
                .endSlot(Optional.of(startSlot + 2630000L)) // 1 month in seconds
                .votingPowerAsset(Optional.empty())
                .team("CF")
                .votingEventType(USER_BASED)
                .schemaVersion(V1)
                .allowVoteChanging(true)
                .categoryResultsWhileVoting(false)
                .highLevelResultsWhileVoting(true)
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created CF-Summit 2023 event: " + createEventCommand;
    }

    @ShellMethod(key = "create-best-wallet-category-pre-prod", value = "Create a CF-Summit 2023 Best Wallet category on a PRE-PROD network.")
    public String createBestWalletCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2023 Best Wallet category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("fe74fb7b-94f6-4777-af72-f086cba20e36")
                .name("Eternl")
                .build();

        Proposal n2 = Proposal.builder()
                .id("85ea4f52-919e-47fa-b92e-0e117ddbebda")
                .name("Nami")
                .build();

        Proposal n3 = Proposal.builder()
                .id("71fab788-0ca9-49b6-aa16-2d7578767e71")
                .name("Typhon")
                .build();

        Proposal n4 = Proposal.builder()
                .id("a30267e1-314c-4801-aa95-b03dd4d6856e")
                .name("Lace")
                .build();

        Proposal n5 = Proposal.builder()
                .id("940e7dcf-7870-46ac-b7c8-423b312f3c14")
                .name("Daedalus")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BEST_WALLET")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(List.of(n1, n2, n3, n4, n5))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "create-best-dex-category-pre-prod", value = "Create a CF-Summit 2023 Best DEX category on a PRE-PROD network.")
    public String createBestDEXCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2023 Best DEX category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("be79ce1f-3cf1-4335-bd07-98f6f24f0f12")
                .name("Sundae Swap")
                .build();

        Proposal n2 = Proposal.builder()
                .id("fd5b1489-a0bb-4ffa-9663-01a57cccbaed")
                .name("MinSwap")
                .build();

        Proposal n3 = Proposal.builder()
                .id("bee8bbed-1941-43d8-9030-90a461c57948")
                .name("SpectrumFinance")
                .build();

        Proposal n4 = Proposal.builder()
                .id("af9ba0df-0c14-4e3a-8e05-c9b944926807")
                .name("MuesliSwap")
                .build();

        Proposal n5 = Proposal.builder()
                .id("f4a2fce1-390a-40e7-a0fb-e9db075999e4")
                .name("WingRiders")
                .build();

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BEST_DEX")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(List.of(n1, n2, n3, n4, n5))
                .build();

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

}
