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
public class CardanoSummit2024PreProdCommands {

    private final static String EVENT_NAME = "CF_SUMMIT_2024";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit04-event-pre-prod", value = "Create a CF-Summit 2024 voting event on a PRE-PROD network.")
    @Order(1)
    public String createCFSummit2024Event() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 on a PRE-PROD network...");

        long base = 69897601; // 00:00:01 UTC 06/09/2024
        long startSlot = base + 50400; // (base + 14 hours) 14:00:01 UTC 06/09/2024
        long endSlot = base + 287999; // (base + 3 days and 8 hours) 08:00:01 UTC 09/09/2024
        long proposalsRevealSlot = endSlot + 3600; // 1 hour after the event ends

        var createEventCommand = CreateEventCommand.builder()
                //CF_SUMMIT_2024_15BCC
                .id(EVENT_NAME + "_" + "15BCC")
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

        return "Created CF-Summit 2024 event: " + createEventCommand;
    }

    @ShellMethod(key = "02_create-ambassador-category-pre-prod", value = "Create a CF-Summit 2024 Ambassador category on a PRE-PROD network.")
    @Order(2)
    public String createAmbassadorCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Ambassador category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("EC30C7AA-70BB-4036-A353-7C306FF31613")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("4582D74F-81D0-4F28-8B18-E70E2A346813")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("7FA87520-0AD7-4797-83C7-4C7625AA58AA")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("135DF392-D603-41B1-8BE1-C103E23816AD")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("AA8C39C4-0795-4F88-A867-3019098D5289")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("AMBASSADOR")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "03_create-blockchain-for-good-category-pre-prod", value = "Create a CF-Summit 2024 Best Blockchain for Good category on a PRE-PROD network.")
    @Order(3)
    public String createBlockchainForGoodCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Blockchain For Good category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("150C277B-22D7-47F4-B626-AA57C338C479")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("7BB2EF27-9BED-4A24-A79B-C79F530D27D2")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("7B9888E9-E5BD-42A1-BCF2-E997FE878E8A")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("FD858845-4C44-423C-9898-62836D246E83")
                .name("Option 4")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BLOCKCHAIN_FOR_GOOD")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "04_create-innovation-standards-category-pre-prod", value = "Create a CF-Summit 2024 Innovation & Standards category on a PRE-PROD network.")
    @Order(4)
    public String createInnovationStandarsCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Innovation & Standards category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("5D72F48B-8285-4BAC-8846-595B455A3084")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("BAD03444-C2EF-4E76-B502-807A69713F19")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("AC5A90ED-A2C0-4775-86B4-C47EC39A083B")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("6D538F80-F617-402F-9F6C-0A2E7260D33E")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("F4192CB9-76B8-4C95-96A8-7B1822625877")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("23D9536B-0ECE-4FE5-90D4-FBF05F7A85DD")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("INNOVATION_STANDARDS")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "05_create-dex-category-pre-prod", value = "Create a CF-Summit 2024 DEX category on a PRE-PROD network.")
    @Order(5)
    public String createDEXCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Best DeFi / DEX category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("CFA2481E-937C-433E-98BD-1AAC9F0FA2B9")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("74404AF7-466B-4679-977C-5F5BDE6446CD")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("AD29AA23-00CA-4A4F-B08C-7D4FC8CE69E9")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("58806B5D-4AD5-4C76-9CF4-2C90D068F6BA")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("802936AE-8114-4EB9-B1D9-C1780811CCBC")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("C3897902-424D-4F42-939C-9C37DE7137C5")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

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

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "06_create-developer-tooling-category-pre-prod", value = "Create a CF-Summit 2024 Developer & Tooling category on a PRE-PROD network.")
    @Order(6)
    public String createDeveloperToolingCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Developer & Tooling category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("865CE0B4-ABF9-4700-A53A-C8E84AD53995")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("E6DBAF3C-5DA1-4AEA-9A14-FA81C14CF3C3")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("3DE5D697-2FF5-4197-9694-78B35EA1CCC3")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("6F604592-9908-45B3-BCE2-5C63F183AFFF")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("7CB41A46-AC83-4F5F-B9D2-DC023209D8EE")
                .name("Option 5")
                .build();
        
        Proposal n6 = Proposal.builder()
                .id("86A96F3F-FE17-42E1-9415-9A02DFEFAF66")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("DEVELOPER_TOOLING")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "07_create-educational-influencer-category-pre-prod", value = "Create a CF-Summit 2024 Educational Influencer category on a PRE-PROD network.")
    @Order(7)
    public String createEducationalInfluencerCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Educational Influence category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("46187CD7-07BD-4B6B-B225-DECA839E87C4")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("9605C945-845B-4616-B98C-3807263187F7")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("E81164D1-550A-47B4-8726-2412E4DF42D0")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("9DFF4520-79C1-491A-95F1-C79B583355D7")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("BBCE94A0-3FDB-4EDA-B0B7-EE256E8B157F")
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

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "08_create-nft-digital-collectibles-category-pre-prod", value = "Create a CF-Summit 2024 NFT & Digital Collectibles category on a PRE-PROD network.")
    @Order(8)
    public String createNFTDigitalCollectiblesCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 NFT & Digital Collectibles category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("A31E6D75-2E84-4862-8032-B2BF7BB1887D")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("7D6EF0EB-8067-4B3A-84F4-1947029E9700")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("B70BD431-3C20-4680-8F79-D3A049CD154B")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("23B353B8-AB9E-46EA-B8E3-3F7B88E2E461")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B3BF6FBB-2838-4C39-850D-19F40E19F1AD")
                .name("Option 5")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("NFT_DIGITAL_COLLECTIBLES")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "09_create-infrastructure-platform-category-pre-prod", value = "Create a CF-Summit 2024 Infrastructure Platform category on a PRE-PROD network.")
    @Order(9)
    public String createInfrastructurePlatformCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 Infrastructure Platform category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("4C43B867-335E-4B75-A06F-D90F675D6B7C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("C9C434CA-8DC8-462A-8E4D-1200B388F585")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("70C455C2-7DCF-48A0-92F1-61B88B8EB0CE")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("7B5CB884-74E5-4B01-A591-4FAA5768F143")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("A4D294D8-C40D-401C-949C-6DBCD0627759")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("6B26B538-9E16-4EA5-8AA2-0120FDBA30F2")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
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

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "10_create-dao-tooling-governance-category-pre-prod", value = "Create a CF-Summit 2024 DAO Tooling & Governance category on a PRE-PROD network.")
    @Order(10)
    public String createDAOToolingGovernanceCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 DAO Tooling & Governance category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("4D831658-C079-4A99-B08C-6180F18652A1")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("1846F0B9-1616-43B2-A919-CF70AEDAB356")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("0D81F908-2C90-4811-A4DE-ED2E61302AE1")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("5278A5CC-AF3D-41A4-837E-10DFDD0CAB63")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("D1C06574-6619-4563-BD6B-6FE297A3E777")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("1A75AED2-2A10-4558-9BE2-31F559242A43")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("DAO_TOOLING_GOVERNANCE")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "11_create-defi-platform-category-pre-prod", value = "Create a CF-Summit 2024 DeFi Platform category on a PRE-PROD network.")
    @Order(11)
    public String createDEFICategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 DeFi Platform category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("8FE32CE4-8B75-4815-BDE5-DCC197945671")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("00CE32E2-60B3-4052-9C83-8A1547C10A63")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("648E39DB-AB35-4072-9BAD-E39450961DD8")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("33D87F23-7D57-41B2-86E3-D5C91BFAEC35")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("D4C62649-9CC3-4288-9C21-62EA8F5899F1")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("EE456364-EA59-4F06-BC37-922572B79404")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("DEFI")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "12_create-sspo-category-pre-prod", value = "Create a CF-Summit 2024 SSPO category on a PRE-PROD network.")
    @Order(12)
    public String createSSPOCategory(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        log.info("Creating CF-Summit 2024 SSPO category on a PRE-PROD network...");

        Proposal n1 = Proposal.builder()
                .id("470FFE2B-18B6-4ACD-8DBE-0E8B7B2B761C")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("B56B2068-7FC6-4ECB-B339-8BB6F1DCA6F9")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("3FB24A88-424F-4D73-A96C-617533D7DB1C")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("A19FF565-895D-46A4-A86D-2FF16FE796E2")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("82A8C4C1-0B86-4682-A9B0-6B88E75AC0E7")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("D83833A3-9F99-4859-B0B7-545E3827FD09")
                .name("Option 6")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("SSPO")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2024 category: " + createCategoryCommand;
    }

}
