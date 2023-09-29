package org.cardano.foundation.voting.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class BaseTest {

    @LocalServerPort
    private int serverPort;
    @Value("${aws.sns.accessKeyId}")
    private String awsAccessKeyId;
    @Value("${aws.sns.secretAccessKey}")
    private String awsSecretAccessKey;
    @Value("${aws.sns.region}")
    private String awsRegion;
    private LocalStackContainer localstackContainer;
    private SnsClient snsClient;
    private WireMockServer wireMockServer;

    @BeforeAll
    public void setUp() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();

        String responseBody = "[{\"id\": \"TEST_EVENT_1337\", \"finished\": false}, {\"notStarted\": false, \"active\": true}]";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/api/reference/event"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)));

        /*
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.11.3");

        localstackContainer = new LocalStackContainer(localstackImage)
                .withServices(SNS);
        localstackContainer.start();

        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey)
        );

        snsClient = SnsClient.builder()
                .endpointOverride(localstackContainer.getEndpointOverride(SNS))
                .credentialsProvider(credentialsProvider)
                .region(Region.of(awsRegion))
                .build();
         */

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    public void tearDown() {
        // Stop and remove the Localstack container after tests
        if (localstackContainer != null) {
            localstackContainer.stop();
        }
    }
}