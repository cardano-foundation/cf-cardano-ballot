package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.crypto.VerificationKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.service.transaction_submit.L1SubmissionService;
import org.springframework.core.annotation.Order;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.cardano.foundation.voting.domain.CardanoNetwork.MAIN;
import static org.cardano.foundation.voting.domain.SchemaVersion.V11;
import static org.cardano.foundation.voting.domain.TallyMode.CENTRALISED;
import static org.cardano.foundation.voting.domain.TallyType.HYDRA;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class CardanoSummit2023ProdCommands {

    private final static String EVENT_NAME = "CARDANO_SUMMIT_AWARDS_2023";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "01_create-cf-summit03-event-prod", value = "Create a CF-Summit 2023 voting event on a PROD network.")
    @Order(1)
    public String createCFSummit2023Event() {
        if (network != MAIN) {
            return "This command can only be run on MAIN network!";
        }

        log.info("Creating CF-Summit 2023 on a MAIN network...");

        long startSlot = 104865309;
        long endSlot = 105502449;
        long proposalsRevealSlot = 107576110;

        var partiesVerificationKeys = Stream.of("5820a9f5c4f7c861ea949bf8af7b52d9b97dc648f21eb9473a54addf202261e05644",
                "5820e0546bf97a1f578091c9b42d0e4b67ec62fd342a1cedae0fb3282e37a7f88865"
                )
                .map(VerificationKey::new)
                .map(KeyGenUtil::getKeyHash)
                .toList();

        var hydraTallyConfig = new HydraTallyConfig(
                "cardano-foundation/hydra-tally",
                "Experimental Hydra Tally Contract",
                "0.0.1",
                "59082a010000323232323232323232232232232232232222325333011323253330133370e90011809000899191919191919191919191919191919191919299981319b87480000344c8c8c8c94ccc0a8ccc02c05004805854ccc0a80044cc030dd618099814180a981400d8010a5014a06600e6eb0c020c09cc050c09c0688cc028090004cdd2a4000660586ea4dcc010198161ba9373003c660586ea4dcc00e198161ba60014bd7019198008008011129998160008a5eb7bdb1804c8c8c8cccc024cc014014008cc88c8cc0040052f5bded8c044a66606600226606866ec0dd49b98005375000897adef6c6013232323253330343375e6600e012004980103d8798000133038337606ea4dcc0049ba8008005153330343372e01200426607066ec0dd49b98009375001000626607066ec0dd49b9800237500026600c00c0066eb4c0d400cdcc9bae303300230370023035001375a6062606460646064606460646064605400600e44466e95200033033375066e000080052f5c000e6e64dd718181818981898189818981898148011818001181700099805008129998139806980b1812800899299981419b8748010c09c0044c8c8c8c94ccc0b00044cdd2a40006606000697ae014c0103d87a8000533302b3372e0466e64dd7180b18148010a99981599b9701f37326eb8c0c0c0c4c0c4c0c4c0c4c0a40084cdcb8109b99375c60346052004294052819299981599b87480000044c8c8c8c8c8c8c8c8c8c8c8c8c8c94ccc0f0c0fc00852616375a607a002607a0046e64dd7181d800981d8011b99375c607200260720046eb8c0dc004c0dc008dcc9bae3035001303500237326eb8c0cc004c0cc008dcc9bae30310013029002163029001302e001302600116301030253016302500114c0103d87a8000132323232533302a33300b0140120161533302a00213300c375860266050602a6050036002294052819ba548000cc0b4dd49b980213302d37526e6007ccc0b4dd49b9801d3302d374c00497ae0330063758600e604c6026604c03246601204600266644464666002002008006444a66606000420022666006006606600466446600c0020046eacc0c8008004c8cc004004008894ccc0b000452f5c026605a6e98dd5981718179817981798139817000998010011817800a5eb7bdb18088cccc018008004888cdd2a4000660606ea0cdc0001000a5eb80010cc02804094ccc09cc034c058c0940044c94ccc0a0cdc3a4008604e002264646464a666058002266e952000330300034bd700a60103d87a8000533302b3372e0466e64dd7180b18148010a99981599b9702137326eb8c068c0a40084cdcb80f9b99375c6028605200429405281807800981700098130008b18081812980b18128008a6103d87a8000223322533302933720004002298103d8798000153330293371e0040022980103d87a800014c103d87b8000300300230030012373000244446466600200200a008444a66605a0042002264666008008606200666664444646600200200e44a66606800226606a66ec0dd49b98006375000a97adef6c6013232323253330353375e6600e014004980103d8798000133039337606ea4dcc0051ba8009005153330353372e01400426464a66606e66e1d200000113303b337606ea4dcc006181e181a8010028802981a80099980400500480089981c99bb037526e60008dd4000998030030019bad303600337326eb8c0d0008c0e0008c0d8004dcc9bae302c001375a605a00200c00a605e00444646600200200644a66605200229404c8c94ccc0a0c01400852889980200200098168011bae302b001230273028302830283028302830283028302800122323300100100322533302700114a026464a66604c66e3c00801452889980200200098158011bae302900122232323300700123253330263370e90011812800899b8f375c605660480020082c6020604660206046002646600200200844a666050002297ae0132325333027323253330293370e90010008a5114a0604e0026024604a6024604a004266056004660080080022660080080026058004605400264a66604666e1d20003022001132323253330263370e9001181280089bae302b3024001163010302330103023301430230013029001302100116323300100100422533302700114c0103d87a80001323253330263375e6022604800400a266e9520003302a0024bd7009980200200098158011814800911980199802001129998109803800899299981119b8748010c0840044c8c8c8cdd2a40006605200497ae030090013028001302000116300a301f00114c103d87a800023375e00200444646600200200644a66604800229404c8c94ccc08cc014008528899802002000981400118130009119198008008019129998118008a5eb804c8c8c8c94ccc090cdc3a400400226600c00c006266050605260440046600c00c0066044002600a004604e004604a002464a66603a66e1d2000001132323232323232325333028302b002132498c8cc004004008894ccc0a800452613233003003302e0023232375a60560046e64dd7181480098160008b1bab3029001302900237326eb8c09c004c09c008dcc9bae3025001302500237326eb8c08c004c06c00858c06c0048c8c94ccc074cdc3a40080022944528180d8009802180c800980c0059bac30013016300330160092301d301e301e0013758600260286002602800e46036002603200260220022c600260200064602e603000229309b2b19299980899b874800000454ccc050c03c00c52616153330113370e90010008a99980a18078018a4c2c2c601e0046e64dd70009b99375c0026e64dd70009bac001375c0024600a6ea80048c00cdd5000ab9a5573aaae7955cfaba05742ae881",
                "1389ddd7070bd334a572825204e068506ad25ab760c725daa8c0eb94",
                "Aiken",
                "v1.0.20-alpha+49bd4ba",
                partiesVerificationKeys,
                "v2"
        );

        var tallyCommand = new TallyCommand(
                "Hydra_Tally_Experiment",
                "",
                HYDRA,
                CENTRALISED,
                1,
                hydraTallyConfig
        );
        
        CreateEventCommand createEventCommand = CreateEventCommand.builder()
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
                .tallies(List.of(tallyCommand))
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
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
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created CF-Summit 2023 category: " + createCategoryCommand;
    }

}
