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
import static org.cardano.foundation.voting.domain.SchemaVersion.V1;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CardanoSummit2023ProdCommands {

    private final static String EVENT_NAME = "CF_SUMMIT_2023";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit03-event-prod", value = "Create a CF-Summit 2023 voting event on a PROD network.")
    @Order(1)
    public String createCFSummit2023Event() {
        if (network != MAIN) {
            return "This command can only be run on MAIN network!";
        }

        log.info("Creating CF-Summit 2023 on a MAIN network...");

        long startSlot = 104339709; // 28-09-2023 13:00:00 UTC
        long endSlot = 105117309; // 07-10-2023 13:00:00 UTC
        long proposalsRevealSlot = 105203709; // 08-10-2023 13:00:00 UTC

        CreateEventCommand createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME + "_" + "TEST3")
                .startSlot(Optional.of(startSlot))
                .endSlot(Optional.of(endSlot))
                .votingPowerAsset(Optional.empty())
                .organisers("CF")
                .votingEventType(USER_BASED)
                .schemaVersion(V1)
                .allowVoteChanging(false)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(false)
                .proposalsRevealSlot(Optional.of(proposalsRevealSlot))
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created CF-Summit 2023 event: " + createEventCommand;
    }

    @ShellMethod(key = "02_create-ambassador-category-prod", value = "Create a CF-Summit 2023 Ambassador category on a PROD network.")
    @Order(2)
    public String createAmbassadorCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Ambassador category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("63123e7f-dfc3-481e-bb9d-fed1d9f6e9b9")
                .build();

        Proposal n2 = Proposal.builder()
                .id("0299d93e-93f2-4bc8-9b40-6dd09343c443")
                .build();

        Proposal n3 = Proposal.builder()
                .id("fd477fac-ad16-4d2a-91a4-0a4288d3d7aa")
                .build();

        Proposal n4 = Proposal.builder()
                .id("0b755eaf-a588-441f-a9dd-50c4aa478a90")
                .build();

        Proposal n5 = Proposal.builder()
                .id("2c94cd2e-2ad9-4425-af01-27210afca1e3")
                .build();

        Proposal n6 = Proposal.builder()
                .id("e7d4df4a-8305-4ed8-9e42-6f67442d796e")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("AMBASSADOR")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "03_create-blockchain-for-good-category-prod", value = "Create a CF-Summit 2023 Best Blockchain for Good category on a PROD network.")
    @Order(3)
    public String createBlockchainForGoodCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Blockchain For Good category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("633199b6-ab4c-49bc-afd8-e8c675d145d0")
                .build();

        Proposal n2 = Proposal.builder()
                .id("d4d33796-7372-410a-b640-7dde093f20e5")
                .build();

        Proposal n3 = Proposal.builder()
                .id("2e24b92f-1a34-4799-9eb4-a489be2b63c6")
                .build();

        Proposal n4 = Proposal.builder()
                .id("4cbeb976-20ba-4c20-bdc1-f21bf28c17fd")
                .build();

        Proposal n5 = Proposal.builder()
                .id("71f4f082-4512-4e7f-adc6-6092d1b3aa14")
                .build();

        Proposal n6 = Proposal.builder()
                .id("07d4a8b3-dbfc-412c-9931-a5252db9082d")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BLOCKCHAIN_FOR_GOOD")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "04_create-cips-category-prod", value = "Create a CF-Summit 2023 CIPs category on a PROD network.")
    @Order(4)
    public String createCIPsCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 CIPs category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("4cf1ea70-87dd-45ee-85a0-2d29720725f7")
                .build();

        Proposal n2 = Proposal.builder()
                .id("d5116b52-1e82-4c7f-95e5-2a74a9f75492")
                .build();

        Proposal n3 = Proposal.builder()
                .id("69ee264f-9d02-48ef-98f1-541ffb28756f")
                .build();

        Proposal n4 = Proposal.builder()
                .id("70a88fb6-a87f-4ebd-8719-31c461118f3d")
                .build();

        Proposal n5 = Proposal.builder()
                .id("eaafc7a1-8944-4faf-91dd-5ceefa51e8db")
                .build();

        Proposal n6 = Proposal.builder()
                .id("9cdbde0e-ffd4-4d22-ae3c-17cb63dc89fd")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("CIPS")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "05_create-defi-dex-category-prod", value = "Create a CF-Summit 2023 DeFi / DEX category on a PROD network.")
    @Order(5)
    public String createDeFiDEXCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Best DeFi / DEX category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("f71c438d-8247-4064-a5aa-4d21b54c2a5d")
                .build();

        Proposal n2 = Proposal.builder()
                .id("a5f92606-6e15-4f91-8443-7030eb02a274")
                .build();

        Proposal n3 = Proposal.builder()
                .id("e9d72191-bda4-437e-af4b-2f979bad5c7f")
                .build();

        Proposal n4 = Proposal.builder()
                .id("8cf20a27-8bc8-49f3-8133-ef76e899e1c1")
                .build();

        Proposal n5 = Proposal.builder()
                .id("91871e20-f9aa-422f-9213-3722ac47c1c6")
                .build();

        Proposal n6 = Proposal.builder()
                .id("c7c7ca58-af7e-4f27-a170-070d76707580")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BEST_DEFI_DEX")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "06_create-developer-or-developer-tools-category-prod", value = "Create a CF-Summit 2023 Developer or Developer Tools category on a PROD network.")
    @Order(6)
    public String createBestDeveloperOrDeveloperTools(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Developer or Developer Tools category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("f4d6055f-964e-43b4-bc23-83141ca04f9f")
                .build();

        Proposal n2 = Proposal.builder()
                .id("a00e6d3e-1b06-48f0-b2a0-4f3784e6226c")
                .build();

        Proposal n3 = Proposal.builder()
                .id("58bb4e29-5124-473b-80e1-c5c8ffa57dbb")
                .build();

        Proposal n4 = Proposal.builder()
                .id("a30267e1-314c-4801-aa95-b03dd4d6856e")
                .build();

        Proposal n5 = Proposal.builder()
                .id("ec34567c-2012-4e3b-94ee-8778a6e33a04")
                .build();

        Proposal n6 = Proposal.builder()
                .id("204a8d71-adb1-4fff-b59d-5fb391d0078d")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("BEST_DEVELOPER_OR_DEVELOPER_TOOLS")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "07_create-educational-influencer-category-prod", value = "Create a CF-Summit 2023 Educational Influencer category on a PROD network.")
    @Order(7)
    public String createEducationalInfluencer(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Educational Influence category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("88af463f-0d9c-4738-baef-bdb80f2c374e")
                .build();

        Proposal n2 = Proposal.builder()
                .id("65cf347e-129a-459b-b192-55ae37e03160")
                .build();

        Proposal n3 = Proposal.builder()
                .id("1e609753-a83e-4ff6-9cf8-dd90803f0368")
                .build();

        Proposal n4 = Proposal.builder()
                .id("9b91f3ed-42be-4650-8e43-6d7a416f9591")
                .build();

        Proposal n5 = Proposal.builder()
                .id("702efda6-ceec-413e-8f33-aa206962850c")
                .build();

        Proposal n6 = Proposal.builder()
                .id("e0b5f280-95f4-42aa-8ecb-00b95c2896a4")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("EDUCATIONAL_INFLUENCER")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "08_create-marketplace-category-prod", value = "Create a CF-Summit 2023 Marketplace category on a PROD network.")
    @Order(8)
    public String createMarketPlaceCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 Marketplace category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("33038f64-fff9-44cc-a8e5-d4f5896c8ff6")
                .build();

        Proposal n2 = Proposal.builder()
                .id("953c7970-6f9d-41a0-8556-b83ff7b481fe")
                .build();

        Proposal n3 = Proposal.builder()
                .id("107fc947-85f0-442e-b56f-9c10e8b5631a")
                .build();

        Proposal n4 = Proposal.builder()
                .id("0752dc99-19fa-4f4c-96c4-25ca3a66a12f")
                .build();

        Proposal n5 = Proposal.builder()
                .id("5b2145cd-8740-4254-942f-889eb3671640")
                .build();

        Proposal n6 = Proposal.builder()
                .id("01991af2-3bc9-4818-a745-db7683c8fe37")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("MARKETPLACE")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "09_create-most-impactful-sspo-category-prod", value = "Create a CF-Summit 2023 Most Impactful SSPO category on a PROD network.")
    @Order(9)
    public String createMostImpactfulSPOCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a MAIN network!";
        }

        log.info("Creating CF-Summit 2023 Most Impactful SSPO category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("6e16cdae-7696-4c41-a5f2-de373a17f488")
                .build();

        Proposal n2 = Proposal.builder()
                .id("46ea36b8-15b6-4d31-8c29-946342595756")
                .build();

        Proposal n3 = Proposal.builder()
                .id("e3a130e7-45c9-47c5-a121-fbdebc6c3e9f")
                .build();

        Proposal n4 = Proposal.builder()
                .id("cfe477d2-e7eb-46a7-a8ee-f721da2de399")
                .build();

        Proposal n5 = Proposal.builder()
                .id("6ee41116-b60c-41d2-974c-c3de31b71a83")
                .build();

        Proposal n6 = Proposal.builder()
                .id("e4895e4b-b25a-43d5-9bd0-1dcd23954faa")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("MOST_IMPACTFUL_SSPO")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "10_create-nft-project-category-prod", value = "Create a CF-Summit 2023 NFT Project category on a PROD network.")
    @Order(10)
    public String createNFTProjectCategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 NFT Project category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("623405b4-a845-4130-b406-b4cf4a1a985d")
                .build();

        Proposal n2 = Proposal.builder()
                .id("9074cf60-d413-4c20-a344-a3f894d2e6c0")
                .build();

        Proposal n3 = Proposal.builder()
                .id("1bae816a-b943-4148-ac58-c4081ef8cac5")
                .build();

        Proposal n4 = Proposal.builder()
                .id("c0f06200-b04c-4e08-b00b-e050cdcc205c")
                .build();

        Proposal n5 = Proposal.builder()
                .id("02bd8150-91cd-499d-b94c-c0e7b5fd5dc4")
                .build();

        Proposal n6 = Proposal.builder()
                .id("50d31468-a915-4284-9e3f-e0c6f5c1c90c")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4, n5, n6);
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("NFT_PROJECT")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

    @ShellMethod(key = "11_create-ssi-category-prod", value = "Create a CF-Summit 2023 SSI category on a PROD network.")
    @Order(11)
    public String createSSICategory(@ShellOption String event) {
        if (network != MAIN) {
            return "This command can only be run on a PROD network!";
        }

        log.info("Creating CF-Summit 2023 SSI category on a PROD network...");

        Proposal n1 = Proposal.builder()
                .id("a910a8af-f63b-4190-90fb-1409fd110526")
                .build();

        Proposal n2 = Proposal.builder()
                .id("0a70b72d-1394-4bdd-bf93-e79ceb0c40a6")
                .build();

        Proposal n3 = Proposal.builder()
                .id("f37bf063-15fc-4959-a6a4-0349a7613ede")
                .build();

        Proposal n4 = Proposal.builder()
                .id("57f93799-5123-4ad0-a13f-a7c70387a756")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3, n4);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("SSI")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V1)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

}
