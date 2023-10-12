package org.cardano.foundation.voting.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.cardano.foundation.voting.VotingApp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@EnableJpaRepositories
@EntityScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"test", "dev--preprod"})
@SpringJUnitConfig(classes = VotingApp.class)
public class BaseTest {

    @LocalServerPort
    private int serverPort;
    @Value("${api.test.event.id}")
    protected String eventId;
    @Value("${cardano.network}")
    private String cardanoNetwork;
        private WireMockServer wireMockServer;

    @BeforeAll
    public void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();

        String responseBodyEvent = "[{\"id\": \"" + eventId + "\", \"finished\": false}, " +
                "{\"notStarted\": false, \"active\": true}]";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBodyEvent)));

        String responseBodyEvent1Details = "{" +
                "\"id\": \"" + eventId + "\", " +
                "\"finished\": false, " +
                "\"notStarted\": false, " +
                "\"active\": true, " +
                "\"isStarted\": true, " +
                "\"proposalsReveal\": true, " +
                "\"commitmentsWindowOpen\": true, " +
                "\"allowVoteChanging\": true, " +
                "\"highLevelEventResultsWhileVoting\": true, " +
                "\"highLevelCategoryResultsWhileVoting\": true, " +
                "\"categoryResultsWhileVoting\": true, " +
                "\"votingEventType\": \"STAKE_BASED\", " +
                "\"categories\": [" +
                    "{\"id\": \"CHANGE_SOMETHING\", \"gdprProtection\": false, \"proposals\": [" +
                            "{\"id\": \"YES\", \"name\": \"YES\"}, " +
                            "{\"id\": \"NO\", \"name\": \"NO\"}" +
                        "]" +
                    "}" +
                "]" +
                "}";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event/" + eventId))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBodyEvent1Details)));

        String responseBodyEvent3Details = "{" +
                "\"id\": \"CF_TEST_EVENT_03\", " +
                "\"finished\": false, " +
                "\"notStarted\": false, " +
                "\"active\": true, " +
                "\"isStarted\": true, " +
                "\"proposalsReveal\": true, " +
                "\"commitmentsWindowOpen\": true, " +
                "\"allowVoteChanging\": true, " +
                "\"highLevelEventResultsWhileVoting\": true, " +
                "\"highLevelCategoryResultsWhileVoting\": true, " +
                "\"categoryResultsWhileVoting\": true, " +
                "\"votingEventType\": \"STAKE_BASED\", " +
                "\"categories\": [" +
                "{\"id\": \"CHANGE_MAYBE\", \"gdprProtection\": false, \"proposals\": [" +
                "{\"id\": \"YES\", \"name\": \"YES\"}, " +
                "{\"id\": \"NO\", \"name\": \"NO\"}," +
                "{\"id\": \"MAYBE\", \"name\": \"MAYBE\"}" +
                "]" +
                "}" +
                "]" +
                "}";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event/CF_TEST_EVENT_03"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBodyEvent3Details)));

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event/CF_TEST_EVENT_02"))
                        .willReturn(aResponse()
                                .withStatus(404)));

        String responseBodyTip = "{\"hash\": \"c1bd418bb511b7911f3201802b15fc40722a054143798126e37af2ff143abc8c\", " +
                "\"epochNo\": 97, \"synced\": true, \"network\": \"PREPROD\", \"absoluteSlot\": 40262417}";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/blockchain/tip"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBodyTip)));

        String responseBodyStake = "{" +
                    "\"stakeAddress\": \"stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek\", " +
                    "\"votingPower\": \"10444555666\", " +
                    "\"epochNo\": 97, " +
                    "\"votingPowerAsset\": \"ADA\"" +
                "}";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/account/CF_TEST_EVENT_01/stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBodyStake)));

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    public void tearDown() {
        wireMockServer.stop();
    }
}