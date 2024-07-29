package org.cardano.foundation.voting.api.tests;

import com.bloxbean.cardano.yaci.store.api.blocks.service.BlockService;
import com.bloxbean.cardano.yaci.store.blocks.domain.Block;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.cardano.foundation.voting.VotingLedgerFollowerApp;
import org.cardano.foundation.voting.api.VotingLedgerFollowerAppTest;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingLedgerFollowerAppEndpoints.REFERENCE_DATA_ENDPOINT;
import static org.cardano.foundation.voting.domain.SchemaVersion.V11;
import static org.cardano.foundation.voting.domain.VotingEventType.STAKE_BASED;
import static org.cardano.foundation.voting.domain.VotingPowerAsset.ADA;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@EnableJpaRepositories
@EntityScan
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles({"test", "dev--preprod"})
@SpringJUnitConfig(classes = { VotingLedgerFollowerApp.class, VotingLedgerFollowerAppTest.class } )
public class ReferenceDataTest {

    @LocalServerPort
    protected int serverPort;

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected ProposalRepository proposalRepository;

    @Autowired
    protected BlockService blockService;

    protected WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer((int) (1024 + Math.floor(Math.random() * 25_000)));
        wireMockServer.start();

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
        proposalRepository.deleteAll();
        categoryRepository.deleteAll();
        eventRepository.deleteAll();
    }

    private void useDefaults() {
        Event event = Event.builder()
                .id("CF_TEST_EVENT")
                .organisers("Cardano Foundation")
                .version(V11)
                .votingEventType(STAKE_BASED)
                .allowVoteChanging(true)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(true)
                .votingPowerAsset(VotingPowerAsset.ADA)
                .startEpoch(97)
                .endEpoch(100)
                .proposalsRevealEpoch(100)
                .snapshotEpoch(97)
                .startSlot(412439L)
                .endSlot(416439L)
                .proposalsRevealSlot(412439L)
                .absoluteSlot(412439L)
                .build();

        eventRepository.save(event);

        Proposal proposalYes = Proposal.builder()
                .id("YES")
                .name("YES")
                .build();

        Proposal proposalNo = Proposal.builder()
                .id("NO")
                .name("NO")
                .build();

        Category category = Category.builder()
                .id("CHANGE_SOMETHING")
                .event(event)
                .version(SchemaVersion.V1)
                .absoluteSlot(412439L)
                .gdprProtection(false)
                .proposals(List.of(proposalYes, proposalNo))
                .build();

        proposalYes.setCategory(category);
        proposalNo.setCategory(category);

        categoryRepository.save(category);
        proposalRepository.save(proposalYes);
        proposalRepository.save(proposalNo);

        when(blockService.getLatestBlock()).thenReturn(Optional.empty());

        Block block1 = Block.builder()
                .hash("356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940")
                .epochNumber(101)
                .slot(412162133L)
                .blockBodyHash("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")
                .build();

        when(blockService.getLatestBlock()).thenReturn(Optional.of(block1));

        when(blockService.getBlockByNumber(412162133L)).thenReturn(Optional.of(block1));
    }

    @Test
    public void testGetEventByNameCommitmentWindowOpen() {
        Event event = Event.builder()
                .id("CF_TEST_EVENT_02")
                .organisers("Cardano Foundation")
                .version(V11)
                .votingEventType(STAKE_BASED)
                .allowVoteChanging(true)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(true)
                .votingPowerAsset(VotingPowerAsset.ADA)
                .startEpoch(97)
                .endEpoch(100)
                .proposalsRevealEpoch(100)
                .snapshotEpoch(97)
                .absoluteSlot(412439L)
                .build();

        eventRepository.save(event);

        Proposal proposalYes = Proposal.builder()
                .id("YES")
                .name("YES")
                .build();

        Proposal proposalNo = Proposal.builder()
                .id("NO")
                .name("NO")
                .build();

        Category category = Category.builder()
                .id("CHANGE_SOMETHING")
                .event(event)
                .version(V11)
                .absoluteSlot(412439L)
                .gdprProtection(false)
                .proposals(List.of(proposalYes, proposalNo))
                .build();

        proposalYes.setCategory(category);
        proposalNo.setCategory(category);

        categoryRepository.save(category);
        proposalRepository.save(proposalYes);
        proposalRepository.save(proposalNo);

        when(blockService.getLatestBlock()).thenReturn(Optional.empty());

        Block block = Block.builder()
                .hash("356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940")
                .epochNumber(101)
                .slot(412162133L)
                .blockBodyHash("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")
                .build();

        when(blockService.getLatestBlock()).thenReturn(Optional.of(block));

        when(blockService.getBlockByNumber(412162133L)).thenReturn(Optional.of(block));

        Response response = given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event/CF_TEST_EVENT_02");

        Assertions.assertEquals(200, response.getStatusCode());

        EventPresentation eventPresentation = response.as(EventPresentation.class);
        Assertions.assertEquals("CF_TEST_EVENT_02", eventPresentation.getId());
        Assertions.assertEquals("Cardano Foundation", eventPresentation.getOrganisers());
        Assertions.assertTrue(eventPresentation.isCommitmentsWindowOpen());
    }

    @Test
    public void testGetEventByNameCommitmentWindowClosed() {
        Block block = Block.builder()
                .hash("356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940")
                .epochNumber(102)
                .slot(412162133L)
                .blockBodyHash("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")
                .build();

        when(blockService.getLatestBlock()).thenReturn(Optional.of(block));

        Event event2 = Event.builder()
                .id("CF_TEST_EVENT_02")
                .organisers("Cardano Foundation")
                .version(V11)
                .votingEventType(STAKE_BASED)
                .allowVoteChanging(true)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(true)
                .votingPowerAsset(ADA)
                .startEpoch(97)
                .endEpoch(100)
                .proposalsRevealEpoch(101)
                .snapshotEpoch(97)
                .absoluteSlot(412439L)
                .build();

        eventRepository.save(event2);

        Proposal proposalYes = Proposal.builder()
                .id("YES")
                .name("YES")
                .build();

        Proposal proposalNo = Proposal.builder()
                .id("NO")
                .name("NO")
                .build();

        Category category = Category.builder()
                .id("CHANGE_SOMETHING")
                .event(event2)
                .version(V11)
                .absoluteSlot(412439L)
                .gdprProtection(false)
                .proposals(List.of(proposalYes, proposalNo))
                .build();

        proposalYes.setCategory(category);
        proposalNo.setCategory(category);

        categoryRepository.save(category);
        proposalRepository.save(proposalYes);
        proposalRepository.save(proposalNo);

        Response response = given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event/CF_TEST_EVENT_02");

        Assertions.assertEquals(200, response.getStatusCode());

        EventPresentation eventPresentation = response.as(EventPresentation.class);
        Assertions.assertEquals("CF_TEST_EVENT_02", eventPresentation.getId());
        Assertions.assertEquals("Cardano Foundation", eventPresentation.getOrganisers());
        Assertions.assertTrue(eventPresentation.isCommitmentsWindowOpen());
    }

    @Test
    public void testGetEventByWrongName() {
        useDefaults();

        given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event/CF_TEST_EVENT_05")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetEvents() {
        useDefaults();

        Response response = given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event");

        Assertions.assertEquals(200, response.getStatusCode());

        List<EventAdditionalInfo> events = response.jsonPath().getList(".", EventAdditionalInfo.class);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("CF_TEST_EVENT", events.get(0).id());
    }

}
