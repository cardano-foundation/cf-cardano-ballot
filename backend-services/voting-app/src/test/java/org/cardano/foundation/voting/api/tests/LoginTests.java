package org.cardano.foundation.voting.api.tests;

import lombok.val;
import org.cardano.foundation.voting.api.BaseTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints.LOGIN_ENDPOINT;
import static org.cardano.foundation.voting.domain.web3.WalletType.CARDANO;
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
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType: "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        val publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        val signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a90a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840ad2bf6e1930a98848e586e957e108039d2cad34a778e9af45299e54a9cb84fe1c5a41700da7c2a7c041ff3ade25199454e62f314f57067d603885c9a809f0b01";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, CARDANO.name())
                .when().get(LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expiresAt", notNullValue());
    }

    @Test
    public void testLoginInvalidStakeAddressInSignature() {
        /*
            Signed message with different stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
                "walletType: "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a80a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f7465737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a3773376676687034306539387173757364397a37656b222c0a202020202020202020202020202020202277616c6c6574547970653a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840a9880fef794b0986741d450d949b93d6b72ef9e522d34b1428b55832bba0697231db01c730c615b544a16a094811bd5cfdaa3c826c72891dd3d33b10a20f580f";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, CARDANO.name())
                .when().get(LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLoginMissingHeader() {
        given()
                .when().get(LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testLoginUnknownEvent() {
        /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType: "CARDANO",
                "event": "UNKNOWN_EVENT",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a60a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c6574547970653a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a2022554e4b4e4f574f4e5f4556454e54222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a58405a69aa01b360be484e1e3601e04cd6c54107d62992dac1a41a6e041744f856f809eb910788a730ee70a93cb9be47f52b1826b9c4a8f1d407a73622eeacc54b0b";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, CARDANO.name())
                .when().get(LOGIN_ENDPOINT + "/login")
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
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a90a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430313632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840109dbc6f27e934d046c009b86bc8ea2f606fb50c61c1552e4de96e73fd9d1ee7b978ff1d650da99c1726e9c38fc0e62c04e1f9ca71010502e23042a613726c07";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, CARDANO.name())
                .when().get(LOGIN_ENDPOINT + "/login")
                .then()
                .statusCode(400);
    }

}
