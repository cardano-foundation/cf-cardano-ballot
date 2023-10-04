package org.cardano.foundation.voting.api.tests;

import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTests extends BaseTest {

    @Test
    public void testLogin() {
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

        given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expiresAt", notNullValue());
    }

    @Test
    public void testLoginInvalidStakeAddressInSignature() {
        /*
            Signed message with stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "address": "stake_test18rdtrqt94egrn8z7galqe7ec6ze4kvk8taltz58tc7r55hszgxaik",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e0" +
                "872a166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f67696e" +
                "222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f746573743" +
                "13872647472717439346567726e387a3767616c7165376563367a65346b766b3874616c747a353874633772353568737a67" +
                "7861696b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250524550524" +
                "f44222c22726f6c65223a22564f544552227d7d5840ef0f214340ca55e458ffd8a56e47a2afec42bbbbb0aaba049cc34879" +
                "f8776a86e6d9c08c643de2f51f8f66626cdb88cc71d1953f38796f6899d063b449d8510e";

        given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLoginMissingHeader() {
        given()
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testLoginUnkownEvent() {
        /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "address": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "event": "CF_TEST_EVENT_02",
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
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3032222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d5840e5881a9491c2115ad3ef1a891d4d56d63c1a1532b5ba0" +
                "7de7a5917cb782e5a2596cf5858a3e95fd8fdd0e1c24235fde96528bd828d7a3dd7ef932e4a79895505";

        given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLoginSlotOutdated() {
        /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40162406",
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
                "96e222c22736c6f74223a223430313632343036222c2264617461223a7b2261646472657373223a227374616b655f7465" +
                "737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a37733766766870343065393871737" +
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d5840465c2557604ab1ab521c131ec3c8ef6d1d0dd89e9e290" +
                "72002c854bbadd149ed541ad2b737932e764d5cc78eee7c124fc3f50a5c6be5df7e6563eca18e604f0e";

        given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }
}
