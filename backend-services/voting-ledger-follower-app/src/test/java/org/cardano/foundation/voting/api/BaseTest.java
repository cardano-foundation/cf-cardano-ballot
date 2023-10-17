package org.cardano.foundation.voting.api;

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

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@EnableJpaRepositories
@EntityScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"test", "dev--preprod"})
@SpringJUnitConfig(classes = VotingLedgerFollowerApp.class)
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

    private WireMockServer wireMockServer;

    @BeforeAll
    public void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();

        String accountResponse = "{" +
                "    \"active\": true,\n" +
                "    \"controlled_amount\": \"12695385\",\n" +
                "    \"pool_id\": \"pool1pu5jlj4q9w9jlxeu370a3c9myx47md5j5m2str0naunn2q3lkdy\",\n" +
                "    \"rewards_sum\": \"95385\",\n" +
                "    \"reserves_sum\": \"0\",\n" +
                "    \"withdrawable_amount\": \"12695385\",\n" +
                "    \"withdrawals_sum\": \"0\",\n" +
                "    \"treasury_sum\": \"0\"\n" +
                "  }";

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/accounts/stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(accountResponse)));

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/accounts/stake_test1uq0zsej7gjyft8sy9dj7sn9rmqdgw32r8c0lpmr6xu3tu9szp6qre"))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                .withStatus(404)));

        String latestBlockResponse = "{" +
                "\"time\": 1641338934," +
                "\"height\": 15243593," +
                "\"hash\": \"4ea1ba291e8eef538635a53e59fddba7810d1679631cc3aed7c8e6c4091a516a\"," +
                "\"slot\": 412162133," +
                "\"epoch\": 425," +
                "\"epoch_slot\": 12," +
                "\"slot_leader\": \"pool1pu5jlj4q9w9jlxeu370a3c9myx47md5j5m2str0naunn2qnikdy\"," +
                "\"size\": 3," +
                "\"tx_count\": 1," +
                "\"output\": \"128314491794\"," +
                "\"fees\": \"592661\"," +
                "\"block_vrf\": \"vrf_vk1wf2k6lhujezqcfe00l6zetxpnmh9n6mwhpmhm0dvfh3fxgmdnrfqkms8ty\"," +
                "\"op_cert\": \"da905277534faf75dae41732650568af545134ee08a3c0392dbefc8096ae177c\"," +
                "\"op_cert_counter\": \"18\"," +
                "\"previous_block\": \"43ebccb3ac72c7cebd0d9b755a4b08412c9f5dcb81b8a0ad1e3c197d29d47b05\"," +
                "\"next_block\": \"8367f026cf4b03e116ff8ee5daf149b55ba5a6ec6dec04803b8dc317721d15fa\"," +
                "\"confirmations\": 4698" +
                "}";

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/blocks/latest"))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(latestBlockResponse)));

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/yaci-api/blocks/latest"))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(latestBlockResponse)));

        String transactionResponse = "{" +
                "\"hash\": \"1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477\"," +
                "\"block\": \"356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940\"," +
                "\"block_height\": 123456," +
                "\"block_time\": 1635505891," +
                "\"slot\": 42000000," +
                "\"index\": 1," +
                "\"output_amount\": [" +
                "{" +
                "\"unit\": \"lovelace\"," +
                "\"quantity\": \"42000000\"" +
                "}," +
                "{" +
                "\"unit\": \"b0d07d45fe9514f80213f4020e5a61241458be626841cde717cb38a76e7574636f696e\"," +
                "\"quantity\": \"12\"" +
                "}" +
                "]," +
                "\"fees\": \"182485\"," +
                "\"deposit\": \"0\"," +
                "\"size\": 433," +
                "\"invalid_before\": null," +
                "\"invalid_hereafter\": \"13885913\"," +
                "\"utxo_count\": 4," +
                "\"withdrawal_count\": 0," +
                "\"mir_cert_count\": 0," +
                "\"delegation_count\": 0," +
                "\"stake_cert_count\": 0," +
                "\"pool_update_count\": 0," +
                "\"pool_retire_count\": 0," +
                "\"asset_mint_or_burn_count\": 0," +
                "\"redeemer_count\": 0," +
                "\"valid_contract\": true" +
                "}";

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/yaci-api/txs/1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477"))
                        .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(transactionResponse)));

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/yaci-api/txs/23ab9463463eb149054d22249433f1bfd5acbbf8af38cc64f3840b0491230880"))
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
