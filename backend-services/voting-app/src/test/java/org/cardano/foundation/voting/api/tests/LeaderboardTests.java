package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class LeaderboardTests extends BaseTest {

    @Test
    public void testIsHighLevelEventLeaderBoardAvailable() {
        given()
                .when()
                .head(VotingAppEndpoints.LEADERBOARD_ENDPOINT + "/event/CF_TEST_EVENT_01")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetEventLeaderBoard() {
        Response response = given()
                .when()
                .get(VotingAppEndpoints.LEADERBOARD_ENDPOINT + "/" +
                        "CF_TEST_EVENT_03");

        Assertions.assertEquals(200, response.getStatusCode());
        Leaderboard.ByEventStats leaderboard = response.as(Leaderboard.ByEventStats.class);
        Assertions.assertEquals(leaderboard.getTotalVotingPower(), "8151");
        Assertions.assertEquals(leaderboard.getEvent(), "CF_TEST_EVENT_03");
        Assertions.assertEquals(leaderboard.getTotalVotesCount(), 3);
    }

    @Test
    public void testGetCategoryLeaderBoard() {
        given()
                .when()
                .get(VotingAppEndpoints.LEADERBOARD_ENDPOINT + "/" +
                        "CF_TEST_EVENT_01" + "/" + "CHANGE_SOMETHING")
                .then()
                .statusCode(200);
    }

    // TODO: I would expect that test should return 404, but it returns 400
    @Test
    public void testGetCategoryLeaderBoardOfUnknownCategory() {
        given()
                .when()
                .get(VotingAppEndpoints.LEADERBOARD_ENDPOINT + "/" +
                        "CF_TEST_EVENT_01" + "/" + "CHANGE_NOTHING")
                .then()
                .statusCode(400);
    }

    // TODO: I would expect that test should return 404, but it returns 400
    @Test
    public void testGetCategoryLeaderBoardOfUnknownEvent() {
        given()
                .when()
                .get(VotingAppEndpoints.LEADERBOARD_ENDPOINT + "/" +
                        "CF_TEST_EVENT_02" + "/" + "CHANGE_SOMETHING")
                .then()
                .statusCode(400);
    }
}
