package org.cardano.foundation.voting.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class BaseTest {

    @LocalServerPort
    private int serverPort;

    private WireMockServer wireMockServer;

    @BeforeAll
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        WireMock.configureFor("localhost", wireMockServer.port());

        String responseBody = "[{\"id\": \"TEST_EVENT_1337\", \"finished\": false}, {\"notStarted\": false, \"active\": true}]";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody))
        );

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    public void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

}