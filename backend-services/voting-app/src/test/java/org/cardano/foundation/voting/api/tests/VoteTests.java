package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints;
import org.cardano.foundation.voting.domain.UserVotes;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoteTests extends BaseTest {

    @Test
    @Order(1)
    public void testCastVote() {
        /*
            Signed message:
            {
              "action": "CAST_VOTE",
              "actionText": "Cast Vote",
              "data": {
                "id": "2658fb7d-cd12-48c3-bc95-23e73616b79f",
                "address": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "event": "CF_TEST_EVENT_01",
                "category": "CHANGE_SOMETHING",
                "proposal": "YES",
                "network": "PREPROD",
                "votedAt": "40262406",
                "votingPower": "10444555666"
              },
              "slot": "40262406",
              "uri": "https://evoting.cardano.org/voltaire"
            }
        */
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e0872a166686173686564f459017f7b22616374696f6e223a22434153545f564f5445222c22616374696f6e54657874223a224361737420566f7465222c2264617461223a7b226964223a2232363538666237642d636431322d343863332d626339352d323365373336313662373966222c2261646472657373223a227374616b655f7465737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a3773376676687034306539387173757364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c2263617465676f7279223a224348414e47455f534f4d455448494e47222c2270726f706f73616c223a22594553222c226e6574776f726b223a2250524550524f44222c22766f7465644174223a223430323632343036222c22766f74696e67506f776572223a223130343434353535363636227d2c22736c6f74223a223430323632343036222c22757269223a2268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265227d5840d4821a7870be3d3e5180a056b02ec31c7e77f8d50968819c7c79adbe46433ed88ae3ef13efa2202a2f29d499ee61d0b8377fd1d4b68ab71deeff0028b87a9707";

        given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().post(VotingAppEndpoints.VOTE_ENDPOINT + "/cast")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    public void testGetVotes() {
        // perform login first
                /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "address": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e" +
                "0872a166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f676" +
                "96e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f7465" +
                "737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a37733766766870343065393871737" +
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d58402a17cf6a88b6700c671d45ee67abdc863bea521425fef" +
                "dad6a6421a554ecdca9fd39f1acc713b35c655e8f8d519b50b7d80899e6d1af8733117da8c2f68a480d";

        // Get bearer token from login
        Response response = given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login");

        String accessToken = response.jsonPath().getString("accessToken");
        // Get the votes
        Response votesResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/votes/" + eventId);

        Assertions.assertEquals(200, votesResponse.getStatusCode());

        List<UserVotes> votes = votesResponse.jsonPath().getList(".", UserVotes.class);
        Assertions.assertEquals(1, votes.size());
    }

    @Test
    @Order(3)
    public void testGetVotesWithAnotherLogin() {
        // perform login first
                /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "address": "stake_test1urljm37nvmexrtvyekg04ue7c00fvk75fw98jhfc9kfhe8c4zrt6y",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9f032720062158204c386f51120072d8b13f6fc7f382ce826303577f46d593c833be1186c5495ce7";
        String signature = "84584aa3012704581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9f6761646472657373581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9fa166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f67696e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f746573743175726c6a6d33376e766d657872747679656b67303475653763303066766b3735667739386a686663396b6668653863347a72743679222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250524550524f44222c22726f6c65223a22564f544552227d7d5840ac25951188c5dd34204808e3ad0e404cf175abad672ffb643cbc928ee7e287bbfe40946970f1263e6efa3090e018ec01dc01a62bd717ab032b484f071d3f2401";

        // Get bearer token from login
        Response response = given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login");

        String accessToken = response.jsonPath().getString("accessToken");
        // Get the votes
        Response votesResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/votes/" + eventId);

        Assertions.assertEquals(200, votesResponse.getStatusCode());

        List<UserVotes> votes = votesResponse.jsonPath().getList(".", UserVotes.class);
        Assertions.assertEquals(0, votes.size());
    }
}
