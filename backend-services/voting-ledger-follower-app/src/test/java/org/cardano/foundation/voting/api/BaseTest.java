package org.cardano.foundation.voting.api;

import com.bloxbean.cardano.yaci.store.blocks.domain.Block;
import com.bloxbean.cardano.yaci.store.blocks.service.BlockService;
import com.bloxbean.cardano.yaci.store.transaction.domain.TransactionDetails;
import com.bloxbean.cardano.yaci.store.transaction.service.TransactionService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.cardano.foundation.voting.VotingLedgerFollowerApp;
import org.cardano.foundation.voting.domain.SchemaVersion;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.VotingPowerAsset;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.MerkleRootHash;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.repository.CategoryRepository;
import org.cardano.foundation.voting.repository.EventRepository;
import org.cardano.foundation.voting.repository.MerkleRootHashRepository;
import org.cardano.foundation.voting.repository.ProposalRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
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

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@EnableJpaRepositories
@EntityScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"test", "dev--preprod"})
@SpringJUnitConfig(classes = { VotingLedgerFollowerApp.class, VotingLedgerFollowerAppTest.class } )
public class BaseTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private MerkleRootHashRepository merkleRootHashRepository;

    @Autowired
    private BlockService blockService;

    @Autowired
    private TransactionService transactionService;

    private WireMockServer wireMockServer;

    @BeforeAll
    public void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();

        String accountResponse = """
                {   "active": true,
                    "controlled_amount": "12695385",
                    "pool_id": "pool1pu5jlj4q9w9jlxeu370a3c9myx47md5j5m2str0naunn2q3lkdy",
                    "rewards_sum": "95385",
                    "reserves_sum": "0",
                    "withdrawable_amount": "12695385",
                    "withdrawals_sum": "0",
                    "treasury_sum": "0"
                  }""";

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/accounts/stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(accountResponse))
        );

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/accounts/stake_test1uq0zsej7gjyft8sy9dj7sn9rmqdgw32r8c0lpmr6xu3tu9szp6qre"))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                .withStatus(404)));

        Event event = Event.builder()
                .id("CF_TEST_EVENT_01")
                .organisers("Cardano Foundation")
                .version(SchemaVersion.V1)
                .votingEventType(VotingEventType.STAKE_BASED)
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

        MerkleRootHash merkleRootHash = MerkleRootHash.builder()
                .eventId("CF_TEST_EVENT_01")
                .merkleRootHash("23ab9463463eb149054d22249433f1bfd5acbbf8af38cc64f3840b0491230880")
                .absoluteSlot(412439L)
                .build();

        merkleRootHashRepository.save(merkleRootHash);

        when(blockService.getLatestBlock()).thenReturn(Optional.empty());

        Block block1 = Block.builder()
                .hash("356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940")
                .epochNumber(412)
                .slot(412162133L)
                .blockBodyHash("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")
                .build();

        when(blockService.getLatestBlock()).thenReturn(Optional.of(block1));

        when(blockService.getBlockByNumber(412162133L)).thenReturn(Optional.of(block1));

        when(transactionService.getTransaction(anyString())).thenReturn(Optional.empty());

        var tx1 = TransactionDetails.builder()
                .hash("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")
                .blockHeight(412162133L)
                .slot(412162133L)
                .build();

        when(transactionService.getTransaction("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477")).thenReturn(Optional.of(tx1));
    }

    @AfterAll
    public void tearDown() {
        wireMockServer.stop();
        proposalRepository.deleteAll();
        categoryRepository.deleteAll();
        eventRepository.deleteAll();
        merkleRootHashRepository.deleteAll();
    }

}
