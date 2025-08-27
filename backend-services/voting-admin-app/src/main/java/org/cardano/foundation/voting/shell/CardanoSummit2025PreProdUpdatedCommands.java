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

import static org.cardano.foundation.voting.domain.CardanoNetwork.PREPROD;
import static org.cardano.foundation.voting.domain.SchemaVersion.V11;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CardanoSummit2025PreProdUpdatedCommands {

    private final static String EVENT_NAME = "CF_SUMMIT_2025";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit05-event-updated-pre-prod", value = "Create a CF-Summit 2025 voting event on a UPDATED-PRE-PROD network.")
    @Order(1)
    public String createCFSummit2025Event() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 on a PRE-PROD network...");

        // pre prod staging event
        long startSlot = 100602000; // 27/08/2025 09:00:00
        long endSlot = 101390400; // 05/09/2025 12:00:00
        long proposalsRevealSlot = 101394000; // 05/09/2025 13:00:00

        var createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + "23BCC")
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

    @ShellMethod(key = "02_create-infrastructure-platform-category-updated-pre-prod", value = "Create a CF-Summit 2025 Infrastructure Platform category on a UPDATED-PRE-PROD network.")
    @Order(2)
    public String createInfrastructurePlatformCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Infrastructure Platform category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("1C70E403-E1E9-4825-8246-8A2EB39E4F69")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("19AB33EB-51AC-4998-8A74-5504409EAA5B")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("15821ABB-C4F2-45E9-9812-B1A16FABE45D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("11C09DAC-EFFC-4D86-8BC3-0C5D5D0DB2EE")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("187B055D-B5A1-4198-9D02-2EF7D2289628")
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

    @ShellMethod(key = "03_create-impactful-spo-category-updated-pre-prod", value = "Create a CF-Summit 2025 Impactful SPO category on a PRE-PROD network.")
    @Order(3)
    public String createImpactfulSPOCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Impactful SPO category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("D7269417-DD20-4B4E-B71C-242B936BDC7A")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("3703C915-B689-4177-8924-846080E866F1")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("026200E0-CCB9-43C6-96DD-84D9273D07EF")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("26871D43-31D3-47DA-9B02-7F3710FD5C09")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("81F7ED68-BB39-418D-99C2-8E75FCA993DF")
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

    @ShellMethod(key = "04_create-impactful-native-asset-category-updated-pre-prod", value = "Create a CF-Summit 2025 Impactful Native Asset category on a PRE-PROD network.")
    @Order(4)
    public String createImpactfulNativeAssetCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Impactful Native Asset category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("AC9E6846-5BAD-4F44-B139-E33C6E87150C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("4BBD1EB4-0412-4059-AC65-7E6A66F9E3BB")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("323FA3BE-0870-43F3-AC55-6BE75D7352C4")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("400A215B-D7A4-4875-8572-C106AF397CAC")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("5F551EBE-A9D6-4BDC-9FA8-862740EFEC49")
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

    @ShellMethod(key = "05_create-developer-tooling-excellence-category-updated-pre-prod", value = "Create a CF-Summit 2025 Developer & Tooling Excellence category on a PRE-PROD network.")
    @Order(5)
    public String createDeveloperToolingExcellenceCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Developer & Tooling Excellence category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("E899C3BF-B9AB-4C27-8045-A31F359F3F57")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("56C1B597-7300-4709-8EC0-9B554726E4D1")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("443AD304-5F2D-4EA6-93BC-A159D6C13F23")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("722DBEDD-5404-4859-B525-0AF4D1F78978")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("C3540297-0508-43F2-8639-69F3DF56290B")
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

    @ShellMethod(key = "06_create-governance-champion-category-updated-pre-prod", value = "Create a CF-Summit 2025 Governance Champion category on a PRE-PROD network.")
    @Order(6)
    public String createGovernanceChampionCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Governance Champion category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("B3AD4801-9721-477B-8CC1-FAF0D1DCCFAD")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("35EF5B7D-B94C-4F73-88C6-7A33089A1A31")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("68ABBE19-1031-4CE9-88DA-772E26147B2A")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("9E7CC44D-ED4C-4141-A39F-B6967AAF0163")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B30932A0-E44E-4311-9A27-8726034DF2E2")
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

    @ShellMethod(key = "07_create-educational-influencer-category-updated-pre-prod", value = "Create a CF-Summit 2025 Educational Influencer category on a PRE-PROD network.")
    @Order(7)
    public String createEducationalInfluencerCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Educational Influencer category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("49BA3D43-0053-4009-BBB1-F1F2D5823C25")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("2023E637-A800-4538-832C-0ED5F2DE8A8C")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("A7153F79-ACE9-4A50-B25C-C90AA742AA9D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("352BD705-83D9-46EB-80E0-D2A207A0E351")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("E6ACB8EB-CC05-4A18-8A83-7C6BA23EA432")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("EDUCATIONAL_INFLUENCER")
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

    @ShellMethod(key = "08_create-dex-category-updated-pre-prod", value = "Create a CF-Summit 2025 DEX category on a PRE-PROD network.")
    @Order(8)
    public String createDEXCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 DEX category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("5F121335-2D9D-493C-801B-5913A48FA11C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("1035C0FC-56FB-48CA-B7C7-A962D5976633")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("86BDC1C8-1303-4125-83AB-CA2B978A19C8")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("A14C00B8-CCCD-4404-8BCC-F6154E5D155A")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B8E0E2B2-272D-4134-94A5-885B0A7BC4EF")
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

    @ShellMethod(key = "09_create-lending-protocol-category-updated-pre-prod", value = "Create a CF-Summit 2025 Lending Protocol category on a PRE-PROD network.")
    @Order(9)
    public String createLendingProtocolCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Lending Protocol category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("C919DD11-63A0-47D3-9BBC-46CD67AF1B1C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("38504A4C-6D52-42A3-A020-9F2968CD9D95")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("644D157C-736C-4AE3-AC94-E29BD23DAC3D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("F8666032-44F3-4627-BAF5-43D974F07C62")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("ABCBBF53-F072-4878-A20B-BF160E96EA11")
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

    @ShellMethod(key = "10_create-aggregator-category-updated-pre-prod", value = "Create a CF-Summit 2025 Aggregator category on a PRE-PROD network.")
    @Order(10)
    public String createAggregatorCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Aggregator category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("6573BD70-6ED5-4EC3-BBF6-26CC150C6CA8")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("43BE8FCD-FF22-40D1-90E7-0B52BFFE9BDD")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("BD241B16-DB36-4955-A191-EB91E91638CB")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("2153E5E7-40B1-4591-8D26-B4CF41C6FF7F")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("253B62CF-50E9-4C87-97F5-7BE8079041F8")
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

    @ShellMethod(key = "11_create-stablecoin-category-updated-pre-prod", value = "Create a CF-Summit 2025 Stablecoin category on a PRE-PROD network.")
    @Order(11)
    public String createStablecoinCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Stablecoin category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("58CE38A1-39AC-4576-B4FF-ED7D4F167920")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("3CDB8A6A-1F24-46C3-96B4-447D786E963F")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("C9D45939-93FB-4372-BE40-DB0D8E7F198D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("DD27584C-37CC-4B35-B9BC-BB7144C1A989")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B30E9D94-8387-4B43-9EEF-BBC4A947C387")
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

    @ShellMethod(key = "12_create-tools-analytics-category-updated-pre-prod", value = "Create a CF-Summit 2025 Tools & Analytics category on a PRE-PROD network.")
    @Order(12)
    public String createToolsAnalyticsCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Tools & Analytics category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("55850CB0-D0BF-4243-84D1-5B776BA8F2FB")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("3C360193-1BD5-4747-92E8-CE4D34452B6B")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("863F3035-2481-47AB-A996-75A8F870BA1F")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("097CF594-960F-4ECF-813C-3EDF4EBF2BC3")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("33EB2501-C07C-4AA9-BA2A-A8D5C6CED363")
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

    @ShellMethod(key = "13_create-other-dapps-category-updated-pre-prod", value = "Create a CF-Summit 2025 Other dApps category on a PRE-PROD network.")
    @Order(13)
    public String createOtherDAppsCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2025 Other dApps category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("938A437D-31E2-4C3E-85ED-0A084BA4D58C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("D0048DB7-07CA-4DEB-ACF5-C168048D97BE")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("396D54CD-AAAF-4DB1-89FB-306BDAB30C00")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("A8B62120-8072-4867-A068-46A8701F2772")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("A53A4552-9550-4D11-9291-375E67FD2E1F")
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
