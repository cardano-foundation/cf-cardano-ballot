package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingLedgerFollowerAppEndpoints.BLOCKCHAIN_DATA_ENDPOINT;

public class BlockchainDataTest extends BaseTest {

    @Test
    public void testTip() {
        given()
                .when()
                .get(BLOCKCHAIN_DATA_ENDPOINT + "/tip")
                .then()
                .statusCode(200);
    }

    @Test
    public void testTxDetails() {
        Response response = given()
                .when()
                .get(BLOCKCHAIN_DATA_ENDPOINT + "/tx-details/1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477");

        Assertions.assertEquals(200, response.getStatusCode());

        TransactionDetails transactionDetails = response.as(TransactionDetails.class);
        Assertions.assertEquals("356b7d7dbb696ccd12775c016941057a9dc70898d87a63fc752271bb46856940", transactionDetails.getBlockHash());
        Assertions.assertEquals("1e043f100dce12d107f679685acd2fc0610e10f72a92d412794c9773d11d8477", transactionDetails.getTransactionHash());
    }

    @Test
    public void testNonExistingTxDetails() {
        given()
                .when()
                .get(BLOCKCHAIN_DATA_ENDPOINT + "/tx-details/23ab9463463eb149054d22249433f1bfd5acbbf8af38cc64f3840b0491230880")
                .then()
                .statusCode(404);
    }
}
