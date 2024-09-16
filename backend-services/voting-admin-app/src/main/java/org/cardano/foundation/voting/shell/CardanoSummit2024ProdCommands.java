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
public class CardanoSummit2024ProdCommands {

    private final static String EVENT_NAME = "CARDANO_SUMMIT_AWARDS_2024";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit04-event-prod", value = "Create a CF-Summit 2024 voting event on a PROD network.")
    @Order(1)
    public String createCFSummit2024Event() {
        if (network != MAIN) {
            return "This command can only be run on MAIN network!";
        }

        log.info("Creating CF-Summit 2024 on a MAIN network...");

        long startSlot = 70531200; // 13/09/2024 08:00:00
        long endSlot = 71136000; // 20/09/2024 08:00:00
        long proposalsRevealSlot = 71143200; // 20/09/2024 10:00:00

        var createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME)
                .startSlot(Optional.of(startSlot))
                .endSlot(Optional.of(endSlot))
                .votingPowerAsset(Optional.empty())
                .organisers("Cardano Foundation")
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

    @ShellMethod(key = "02_create-ambassador-category-prod", value = "Create a CF-Summit 2024 Ambassador category on a PROD network.")
    @Order(2)
    public String createAmbassadorCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Ambassador category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("E4EB1F0C-427A-4B94-B965-C6F46CA09845")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("7D2B42A7-B28B-463D-A673-C992CC462413")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("A50DF74D-C89D-46A1-B90F-DA073CCD5CA3")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("538FBA40-E852-417A-9716-7D794D4BE8DB")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("21910627-D2E5-4429-89EA-102484C1925D")
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

    @ShellMethod(key = "03_create-blockchain-for-good-category-prod", value = "Create a CF-Summit 2024 Best Blockchain for Good category on a PROD network.")
    @Order(3)
    public String createBlockchainForGoodCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Blockchain For Good category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("72F103B0-5DBB-44F9-BC56-AB991B784FCC")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("2976E410-916C-4564-A158-78E04EE03B4C")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("092C7ED2-40EC-4544-AFDB-BCF2A0B9DE64")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("C95EF71B-CA9F-40A5-BF9D-789C78EB8D57")
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

    @ShellMethod(key = "04_create-innovation-standards-category-prod", value = "Create a CF-Summit 2024 Innovation & Standards category on a PROD network.")
    @Order(4)
    public String createInnovationStandarsCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Innovation & Standards category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("2E2676E5-5D72-400C-AF66-ED53A957A86E")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("923437B0-75BA-4AF0-9EB8-43AABBF696C5")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("00DAE5F3-ABCF-41A3-9E5B-70B41A2958BC")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("0E724D70-BBBD-4310-B6E5-EF2D7F5BF154")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("B1727049-7F0F-421C-B1CE-A0CEB29AD189")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("5332AD46-5317-4E02-9069-4908F56881E4")
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

    @ShellMethod(key = "05_create-dex-category-prod", value = "Create a CF-Summit 2024 DEX category on a PROD network.")
    @Order(5)
    public String createDEXCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Best DeFi / DEX category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("882F424F-D628-4C27-B072-076A615C8CB7")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("EB3756EE-4DE6-499F-AD97-24AD799A181A")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("7225D86F-0AE2-4AEF-8678-19F658794D0A")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("BC20C916-BFAA-4347-8EC6-2B6F1E699EB0")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("AE33E741-1E1A-4681-9B11-0949D511F502")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("7AEC510B-FA86-472E-8E92-66C8E71F1BED")
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

    @ShellMethod(key = "06_create-developer-tooling-category-prod", value = "Create a CF-Summit 2024 Developer & Tooling category on a PROD network.")
    @Order(6)
    public String createDeveloperToolingCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Developer & Tooling category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("FB8501FE-0D1C-4D9D-8219-62881819C277")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("040A201A-0E1D-4607-921A-E7FC154DEE0D")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("8B260B9A-5928-487B-91B6-21EBE484A956")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("6A0AEC7B-85AD-45B2-98B8-2E436A718F45")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("6F4D3D8B-531A-4FB3-AC41-CB0937AFE059")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("3F10573E-6564-48BB-851F-A40C9BC6E248")
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

    @ShellMethod(key = "07_create-educational-influencer-category-prod", value = "Create a CF-Summit 2024 Educational Influencer category on a PROD network.")
    @Order(7)
    public String createEducationalInfluencerCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Educational Influence category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("968AEB62-DADD-4D79-B6D6-B2A013982874")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("E3531D81-1F35-42D7-A175-EBC353E0E365")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("C16D8E47-F220-4B51-8024-EA184D867898")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("8ED763AF-39BA-4D88-B933-1E3320DB179A")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("2CE60EE3-BF09-45FC-9DE1-970785C14B5F")
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

    @ShellMethod(key = "08_create-nft-digital-collectibles-category-prod", value = "Create a CF-Summit 2024 NFT & Digital Collectibles category on a PROD network.")
    @Order(8)
    public String createNFTDigitalCollectiblesCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 NFT & Digital Collectibles category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("EDCB0046-E576-4CD4-965C-D442086B2EC5")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("2967D6F9-AE00-4B2D-94DA-82CBDE18FC88")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("68E4D3AB-FA69-4DA5-850F-290A8DC21C89")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("0E39800C-C9AB-4543-B9F8-12BD8ED3808F")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("5CEC4D99-886C-4BE7-B6C8-BEED71682F75")
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

    @ShellMethod(key = "09_create-infrastructure-platform-category-prod", value = "Create a CF-Summit 2024 Infrastructure Platform category on a PROD network.")
    @Order(9)
    public String createInfrastructurePlatformCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 Infrastructure Platform category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("86A61885-556E-4320-AD16-79D87793544A")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("6C6092EB-D04D-411F-BDDE-87DDF71C375A")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("49C9BFC5-70F1-43AB-AC7F-15265F6A20F5")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("CFBDA700-ABB9-420C-9828-798A9BC7B6A9")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("85D2E32F-BD87-4A8A-9736-46E9A64EEC8A")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("C975CC1D-28A3-47F1-8FFE-9E0C897079DA")
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

    @ShellMethod(key = "10_create-dao-tooling-governance-category-prod", value = "Create a CF-Summit 2024 DAO Tooling & Governance category on a PROD network.")
    @Order(10)
    public String createDAOToolingGovernanceCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 DAO Tooling & Governance category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("64C38356-E099-40CF-9F5D-30EED973B89A")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("55C832E9-C3EE-4029-8DC2-F73C29379316")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("B18E350C-D561-41D9-9C72-C42C7BE6DB8D")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("D791ECA4-19C2-45B9-9A3F-DEE509D5ADA0")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("36384CB0-3517-483D-8AA5-AA8F9E51159B")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("ECFA5481-3B31-4DAE-B3B7-145ED3525FEE")
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

    @ShellMethod(key = "11_create-defi-platform-category-prod", value = "Create a CF-Summit 2024 DeFi Platform category on a PROD network.")
    @Order(11)
    public String createDEFICategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 DeFi Platform category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("A37B8E3B-99ED-4596-9349-E31015DCFA43")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("FDB103DC-EA4F-48BD-85EF-877F21C3FEC8")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("9E1B84B1-1F5F-4CD9-92A5-264E30C69BA9")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("5DF10439-EDBE-4AD3-A033-833906220AD8")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("26364D11-DD33-42FA-8AA9-48DB223B660E")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("B1F3D716-D10F-43F2-9462-D5D1445324E6")
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

    @ShellMethod(key = "12_create-sspo-category-prod", value = "Create a CF-Summit 2024 SSPO category on a PROD network.")
    @Order(12)
    public String createSSPOCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2024 SSPO category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("10C26123-3E3F-4503-861B-3EE0E3ADA21A")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("5C1F4FE1-8126-40BB-9C65-CBB9286CC1F0")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("42BBA522-2222-4671-930A-59FC5F1940CB")
                .name("Option 3")
                .build();

        Proposal n4 = Proposal.builder()
                .id("F34DAB95-603B-4F92-A70C-48016C13D28F")
                .name("Option 4")
                .build();

        Proposal n5 = Proposal.builder()
                .id("A3CA5296-A6B3-45BF-B047-11D685B1DDEF")
                .name("Option 5")
                .build();

        Proposal n6 = Proposal.builder()
                .id("D7C37AFD-13A7-4AE3-8312-3888F9A3DB77")
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
