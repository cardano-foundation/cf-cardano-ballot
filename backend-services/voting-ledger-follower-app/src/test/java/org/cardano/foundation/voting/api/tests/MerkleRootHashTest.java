package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.domain.IsMerkleRootPresentResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingLedgerFollowerAppEndpoints.MERKLE_ROOT_HASH_ENDPOINT;

public class MerkleRootHashTest extends BaseTest {

    @Test
    public void testIsValidMerkleRootHash() {
        Response response = given()
                .when()
                .get(MERKLE_ROOT_HASH_ENDPOINT + "/CF_TEST_EVENT_01/23ab9463463eb149054d22249433f1bfd5acbbf8af38cc64f3840b0491230880");

        Assertions.assertEquals(200, response.getStatusCode());
        IsMerkleRootPresentResult result = response.as(IsMerkleRootPresentResult.class);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void testInvalidMerkleRootHash() {
        Response response = given()
                .when()
                .get(MERKLE_ROOT_HASH_ENDPOINT + "/CF_TEST_EVENT_01/faff1f14f56f24e5e112da99f93f36f3c70861930622be759bba100ebff9aea1");

        Assertions.assertEquals(200, response.getStatusCode());
        IsMerkleRootPresentResult result = response.as(IsMerkleRootPresentResult.class);
        Assertions.assertFalse(result.isPresent());
    }

    // Todo: It returns 400. Should return 404.
    @Test
    public void testInvalidEventMerkleRootHash() {
        given()
                .when()
                .get(MERKLE_ROOT_HASH_ENDPOINT + "/CF_TEST_EVENT_05/23ab9463463eb149054d22249433f1bfd5acbbf8af38cc64f3840b0491230880")
                .then()
                .statusCode(400);
    }
}
