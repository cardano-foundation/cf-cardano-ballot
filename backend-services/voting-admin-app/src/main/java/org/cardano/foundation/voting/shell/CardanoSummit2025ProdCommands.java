package org.cardano.foundation.voting.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.Proposal;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.springframework.core.annotation.Order;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.CardanoNetwork.MAIN;
import static org.cardano.foundation.voting.domain.SchemaVersion.V11;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CardanoSummit2025ProdCommands {

    private final static String EVENT_NAME = "CF_SUMMIT_2025";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit05-event-updated-main", value = "Create a CF-Summit 2025 voting event on MAIN.")
    @Order(1)
    public String createCFSummit2025Event() {
        if (network != MAIN) {
            return "This command can only be run on MAIN network!";
        }

        log.info("Creating CF-Summit 2025 on a MAIN network...");

        // mainnet test event
        long startSlot = 166349709; // 15/09/2025 06:00:00
        long endSlot = 166716909; // 19/09/2025 12:00:00
        long proposalsRevealSlot = 166720509; // 19/09/2025 13:00:00

        var createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + "24BCC")
                .startSlot(Optional.of(startSlot))
                .endSlot(Optional.of(endSlot))
                .votingPowerAsset(Optional.empty())
                .organisers("CF")
                .votingEventType(USER_BASED)
                .schemaVersion(V11)
                .allowVoteChanging(false)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(false)
                .proposalsRevealSlot(Optional.of(proposalsRevealSlot))
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created CF-Summit 2025 event: " + createEventCommand;
    }

    @ShellMethod(key = "02_create-infrastructure-platform-category-updated-main", value = "Create a CF-Summit 2025 Infrastructure Platform category on MAIN.")
    @Order(2)
    public String createInfrastructurePlatformCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Infrastructure Platform category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("17B820E3-BC80-437F-8D5B-570CC23F24B4")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("C08E082C-26B4-4AD1-B81D-17A326FC91A1")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("76DA2361-481E-4593-9C65-93584D764775")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("70ECE69D-A66E-438B-8DAF-0BBADC1A56A9")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B89FB33B-04DB-4F13-9BD6-205AC3AFF67D")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("INFRASTRUCTURE_PLATFORM")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "03_create-impactful-spo-category-updated-main", value = "Create a CF-Summit 2025 Impactful SPO category on MAIN.")
    @Order(3)
    public String createImpactfulSPOCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Impactful SPO category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("559077BC-A1B7-462A-8B59-4B6F4B9E2663")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("32315D4A-0609-4F3F-8669-127E83AB9924")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("6700C1A1-46D8-4226-96C5-DCDC5D281FF3")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("D4D699FC-9BF4-4130-A8DE-7D833FF9BC9E")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("C970F862-E058-4933-994F-3FD048C37CF4")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("IMPACTFUL_SPO")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "04_create-impactful-native-asset-category-updated-main", value = "Create a CF-Summit 2025 Impactful Native Asset category on MAIN.")
    @Order(4)
    public String createImpactfulNativeAssetCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Impactful Native Asset category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("63448C3E-822D-4AF9-AB02-E2FB02E9B122")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("30350481-2016-4AE7-AD17-85CB9FD8820A")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("FB4F7DE7-27AC-4FC5-9A3D-D8879BF1B882")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("D9A3DA59-79A9-4FB3-8294-2C255CD012F1")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("C87F7469-7BD6-42A6-9136-E8DF4C255583")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("IMPACTFUL_NATIVE_ASSET")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "05_create-developer-tooling-excellence-category-updated-main", value = "Create a CF-Summit 2025 Developer & Tooling Excellence category on MAIN.")
    @Order(5)
    public String createDeveloperToolingExcellenceCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Developer & Tooling Excellence category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("9631292F-3F7D-4081-8437-126A298F459F")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("50A20202-365A-438F-9934-B800FB675946")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("56C4BF21-860C-43D1-85A0-3D161E315EDE")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("914EACD2-AF80-4948-B81E-F3E2EF3ACA17")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("6C0A0D29-E68B-4F46-92CB-A85692123398")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("DEVELOPER_TOOLING_EXCELLENCE")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "06_create-governance-champion-category-updated-main", value = "Create a CF-Summit 2025 Governance Champion category on MAIN.")
    @Order(6)
    public String createGovernanceChampionCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Governance Champion category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("F3A638C1-31B9-4C2A-8259-42ED7EB609A0")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("68BFFC3A-1708-45AF-A35C-786BAE3F4615")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("D3190A00-FACD-4999-94F5-57D1993E7B2D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("7A855684-1113-4BFC-A909-B701B9E16947")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("8B24C96B-2016-4F25-AC19-B3CEA3708BB5")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("GOVERNANCE_CHAMPION")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "07_create-educational-influencer-category-updated-main", value = "Create a CF-Summit 2025 Educational Influencer category on MAIN.")
    @Order(7)
    public String createEducationalInfluencerCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Educational Influencer category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("E14AC790-A9BD-4BF8-BF7A-BC5BA31E9AFF")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("2BD5E6AB-29B7-4E6C-BC9B-974CE9E1621B")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("A3637048-2A98-4F51-8AAD-6F3E2B7A956C")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("5631123F-0CBC-4E45-911D-37629C07ECDC")
                .name("Option 4")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("AMBASSADORS_EDUCATION")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "08_create-dex-category-updated-main", value = "Create a CF-Summit 2025 DEX category on MAIN.")
    @Order(8)
    public String createDEXCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 DEX category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("F1A2B3C4-D5E6-47F8-9A0B-1C2D3E4F5A6B")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("A2B3C4D5-E6F7-48A9-0B1C-2D3E4F5A6B7C")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("B3C4D5E6-F7A8-49B0-1C2D-3E4F5A6B7C8D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("C4D5E6F7-A8B9-40C1-2D3E-4F5A6B7C8D9E")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("D5E6F7A8-B9C0-41D2-3E4F-5A6B7C8D9E0F")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("DEX")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "09_create-lending-protocol-category-updated-main", value = "Create a CF-Summit 2025 Lending Protocol category on MAIN.")
    @Order(9)
    public String createLendingProtocolCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Lending Protocol category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("A1B2C3D4-E5F6-47A8-9B0C-1D2E3F4A5B6C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("B2C3D4E5-F6A7-48B9-0C1D-2E3F4A5B6C7D")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("C3D4E5F6-A7B8-49C0-1D2E-3F4A5B6C7D8E")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("D4E5F6A7-B8C9-40D1-2E3F-4A5B6C7D8E9F")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("E5F6A7B8-C9D0-41E2-3F4A-5B6C7D8E9F0A")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("LENDING_PROTOCOL")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "10_create-aggregator-category-updated-main", value = "Create a CF-Summit 2025 Aggregator category on MAIN.")
    @Order(10)
    public String createAggregatorCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Aggregator category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("2824EBBD-0CBD-4019-9303-10C236646937")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("22DC7598-8681-48BA-9B7B-E11BE74BE611")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("3FF67210-3A09-4DF0-B714-29F4FA248003")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("BF0C5CE7-D004-4335-A66B-99620D45FB83")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("DC7A387C-F1A2-4C1B-BF78-61D7CE9656C4")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("AGGREGATOR")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "11_create-stablecoin-category-updated-main", value = "Create a CF-Summit 2025 Stablecoin category on MAIN.")
    @Order(11)
    public String createStablecoinCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Stablecoin category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("E1F2A3B4-C5D6-47E8-9F0A-1B2C3D4E5F6A")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("F2A3B4C5-D6E7-48F9-0A1B-2C3D4E5F6A7B")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("A3B4C5D6-E7F8-49A0-1B2C-3D4E5F6A7B8C")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("B4C5D6E7-F8A9-40B1-2C3D-4E5F6A7B8C9D")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("C5D6E7F8-A9B0-41C2-3D4E-5F6A7B8C9D0E")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("STABLECOIN")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "12_create-tools-analytics-category-updated-main", value = "Create a CF-Summit 2025 Tools & Analytics category on MAIN.")
    @Order(12)
    public String createToolsAnalyticsCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Tools & Analytics category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("52027A6C-20D4-4FD5-9A55-8200CE4D2261")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("8C3D2B0D-5F91-4F28-A5B7-B234B7C3A6B7")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("BDD1680C-1F44-4063-8280-C71F173D2B80")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("BB6DDEC4-450F-4FE1-BC55-DF1887C23394")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("028F3529-4700-41DB-98F8-202AE2CDE016")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("TOOLS_ANALYTICS")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "13_create-other-dapps-category-updated-main", value = "Create a CF-Summit 2025 Other dApps category on MAIN.")
    @Order(13)
    public String createOtherDAppsCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2025 Other dApps category on a MAIN network...");

        Proposal n1 = Proposal.builder()
                .id("42FC8ED4-5697-460F-A511-C565AA2A3C12")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("53A71E7F-91C2-4441-B2A1-D22CA35B3E0E")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("FDDCB33B-135E-4848-A9C6-5F0CC79D01F5")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("AB8212DA-A20A-456D-BD2F-F9BD2F4AAC70")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("30118DF7-78A4-408A-85FB-67A8614463D6")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("OTHER_DAPPS")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2025 category: " + createCategoryCommand;
    }

}
