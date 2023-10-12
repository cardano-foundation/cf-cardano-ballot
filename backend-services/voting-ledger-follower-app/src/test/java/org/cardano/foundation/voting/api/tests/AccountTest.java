package org.cardano.foundation.voting.api.tests;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.http.params.CoreConnectionPNames;
import org.cardano.foundation.voting.api.BaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingLedgerFollowerAppEndpoints.ACCOUNT_ENDPOINT;

public class AccountTest extends BaseTest {

    @Test
    public void testFindAccount() {
        String stakeAddress = "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek";
        String eventId = "CF_TEST_EVENT_01";
        given()
                .when()
                .get( ACCOUNT_ENDPOINT + "/" + eventId + "/" + stakeAddress)
                .then()
                .statusCode(200);
    }

    //TODO: returns 400, should return 404
    @Test
    public void testFindAccountNonExistingEvent() {
        String stakeAddress = "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek";
        String eventId = "CF_TEST_EVENT_02";
        given()
                .when()
                .get( ACCOUNT_ENDPOINT + "/" + eventId + "/" + stakeAddress)
                .then()
                .statusCode(400);
    }

        // TODO: hangs in trying again and again to get the stake amount (should return 404)

        @Test
        @Disabled
        public void shouldNotFindAccount() {
            String stakeAddress = "stake_test1uq0zsej7gjyft8sy9dj7sn9rmqdgw32r8c0lpmr6xu3tu9szp6qre";
            String eventId = "CF_TEST_EVENT_01";

            given()
                    .when()
                    .get( ACCOUNT_ENDPOINT + "/" + eventId + "/" + stakeAddress)
                    .then()
                    .statusCode(404);
        }
}
