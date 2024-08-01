package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import lombok.val;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints;
import org.cardano.foundation.voting.domain.UserVotes;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints.LOGIN_ENDPOINT;
import static org.cardano.foundation.voting.resource.Headers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoteTests extends BaseTest {

    private final static String LOGIN_SIGNATURE = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a80a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c6574547970653a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a58407bc8e512a9d754ebdabbc3aa00033ba22f5d5f20048373e087e2cd9fc4b0f186c1696d28bfa48141abddef723f6cb0497fc603c328bfb2c248f60dba271f5208";
    private final static String LOGIN_PUBLIC_KEY = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";

    private static String getAccessToken() {
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
        val response = given()
                .header(X_Login_Signature, LOGIN_SIGNATURE)
                .header(X_Login_PublicKey, LOGIN_PUBLIC_KEY)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
                .when().get(LOGIN_ENDPOINT + "/login");

        return response.jsonPath().getString("accessToken");
    }

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
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
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
        String publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45902b30a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a2022434153545f564f5445222c0a202020202020202020202020202022616374696f6e54657874223a20224361737420566f7465222c0a20202020202020202020202020202264617461223a207b0a20202020202020202020202020202020226964223a202232363538666237642d636431322d343863332d626339352d323365373336313662373966222c0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a202020202020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020202020202270726f706f73616c223a2022594553222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022766f7465644174223a20223430323632343036222c0a2020202020202020202020202020202022766f74696e67506f776572223a20223130343434353535363636220a20202020202020202020202020207d2c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a202020202020202020202020202022757269223a202268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265220a2020202020202020202020207d0a5840665d945e64a38a767c0268279a3c6b155dfe05f9dd9f19c814ec6074b729a913ed7116aa7af98e1608d5b01cccf56e48820e5674259c634f9b01ee4c21fd6002";

        given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
                .header(X_Wallet_Type, WalletType.CARDANO.name())
                .when().post(VotingAppEndpoints.VOTE_ENDPOINT + "/cast")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    public void testGetVotes() {
        // Get bearer token from login
        String accessToken = getAccessToken();
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
        /*
            Signed message with different account:
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

        // Get bearer token from login
        String accessToken = getAccessToken();
        // Get the votes
        Response votesResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/votes/" + eventId);

        Assertions.assertEquals(200, votesResponse.getStatusCode());

        List<UserVotes> votes = votesResponse.jsonPath().getList(".", UserVotes.class);
        Assertions.assertEquals(0, votes.size());
    }

    @Test
    @Order(4)
    public void getVoteReceipt() {
        /*
            Signed message:
            {
              "action": "VIEW_VOTE_RECEIPT",
              "actionText": "Cast Vote",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "category": "CHANGE_SOMETHING",
                "network": "PREPROD",
              }
            }
        */
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e0872a166686173686564f458ed7b22616374696f6e223a22564945575f564f54455f52454345495054222c22616374696f6e54657874223a224361737420566f7465222c22736c6f74223a20223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f7465737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a3773376676687034306539387173757364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c2263617465676f7279223a224348414e47455f534f4d455448494e47222c226e6574776f726b223a2250524550524f44227d7d584075c6bbf365b130b65315748c3c5a112bf5610e90a92b5fa6573a608d49a427b9a4141f424da6df984b97a925073b46fccf0a53650e81dc9cf11b04ef8633350a";

        Response response = given()
                .header("X-CIP93-Signature", signature)
                .header("X-CIP93-Public-Key", publicKey)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/receipt");

        Assertions.assertEquals(200, response.getStatusCode());

        VoteReceipt voteReceipt = response.jsonPath().getObject(".", VoteReceipt.class);
        Assertions.assertNotNull(voteReceipt);
        Assertions.assertEquals(voteReceipt.getEvent(), eventId);
        Assertions.assertEquals(voteReceipt.getCategory(), "CHANGE_SOMETHING");
        Assertions.assertEquals(voteReceipt.getProposal(), "YES");
        Assertions.assertNotNull(voteReceipt.getVotingPower());
    }

    @Test
    @Order(5)
    public void testGetVoteReceiptWithLogin() {
        // Get bearer token from login
        String accessToken = getAccessToken();
        String category = "CHANGE_SOMETHING";
        // Get receipts
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/receipt/" + eventId + "/" + category);

        Assertions.assertEquals(200, response.getStatusCode());
        VoteReceipt voteReceipt = response.jsonPath().getObject(".", VoteReceipt.class);
        Assertions.assertNotNull(voteReceipt);
        Assertions.assertEquals(voteReceipt.getEvent(), eventId);
        Assertions.assertEquals(voteReceipt.getCategory(), "CHANGE_SOMETHING");
        Assertions.assertEquals(voteReceipt.getProposal(), "YES");
    }

    @Test
    @Order(6)
    // TODO: Why is the jwt matcher for this route disabled? That's why the request ends up in a 403.
    public void testVoteChangingAvailable() {
        // Get bearer token from login
        String accessToken = getAccessToken();
        String voteId = "2658fb7d-cd12-48c3-bc95-23e73616b79f";
        given().header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/vote-changing-available/" + eventId + "/" + voteId)
                .then()
                .statusCode(403);
    }

}
