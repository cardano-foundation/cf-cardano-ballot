package org.cardano.foundation.voting.api.tests;

import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.resource.Headers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.resource.Headers.*;
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
                "walletId": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f459019d0a202020207b0a202020202020202022757269223a202268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265222c0a202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a202020202020202020202020226e6574776f726b223a202250524550524f44222c0a20202020202020202020202022726f6c65223a2022564f544552220a20202020202020207d0a2020207d0a5840cf4061003764e1f9976ae5a236ac0d9dc1102b3b0ba804b8ba61980a1a6f3afc24668839964f4d58bb32252741c70ef1e68ce1eb87a66fa683f773acca8b550e";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
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
                "walletId": "stake_test18rdtrqt94egrn8z7galqe7ec6ze4kvk8taltz58tc7r55hszgxaik",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f459019d0a202020207b0a202020202020202022757269223a202268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265222c0a202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374313872647472717439346567726e387a3767616c7165376563367a65346b766b3874616c747a353874633772353568737a677861696b222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a202020202020202020202020226e6574776f726b223a202250524550524f44222c0a20202020202020202020202022726f6c65223a2022564f544552220a20202020202020207d0a2020207d0a584041de5ff750f792235f79f53c6f8a3c042b68f43d75b2ffcce0461139999e59d2a9c015c5914e02dd01d306f33d2ec79cfe7de0220cc3f33a30c592b9d42b9000";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
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
                "event": "UNKNOWON_EVENT",
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
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
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
                "walletId": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f459019d0a202020207b0a202020202020202022757269223a202268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265222c0a202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202022736c6f74223a20223430313632343036222c0a20202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a202020202020202020202020226e6574776f726b223a202250524550524f44222c0a20202020202020202020202022726f6c65223a2022564f544552220a20202020202020207d0a2020207d0a5840dc7ff3cd46bff88419674d8974dcc970aae25ededd9e6d6cdcc097a1d0b05ddfb9796ec4c218858df60d9bb706ec55078b5708154e7d403ae9e6dea683d5b802";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
                .when().get(VotingAppEndpoints.LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }

}
