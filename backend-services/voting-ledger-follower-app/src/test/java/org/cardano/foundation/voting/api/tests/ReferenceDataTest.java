package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.domain.EventAdditionalInfo;
import org.cardano.foundation.voting.domain.presentation.EventPresentation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingLedgerFollowerAppEndpoints.REFERENCE_DATA_ENDPOINT;

public class ReferenceDataTest extends BaseTest {

    @Test
    public void testGetEventByName() {
        Response response = given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event/CF_TEST_EVENT_01");

        Assertions.assertEquals(200, response.getStatusCode());

        EventPresentation eventPresentation = response.as(EventPresentation.class);
        Assertions.assertEquals("CF_TEST_EVENT_01", eventPresentation.getId());
        Assertions.assertEquals("Cardano Foundation", eventPresentation.getOrganisers());
    }

    @Test
    public void testGetEventByWrongName() {
        given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event/CF_TEST_EVENT_05")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetEvents() {
        Response response = given()
                .when()
                .get(REFERENCE_DATA_ENDPOINT + "/event");

        Assertions.assertEquals(200, response.getStatusCode());

        List<EventAdditionalInfo> events = response.jsonPath().getList(".", EventAdditionalInfo.class);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("CF_TEST_EVENT_01", events.get(0).id());
    }
}
