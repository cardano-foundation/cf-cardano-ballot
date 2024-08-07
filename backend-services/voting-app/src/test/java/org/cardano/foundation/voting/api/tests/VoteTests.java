package org.cardano.foundation.voting.api.tests;

import io.restassured.response.Response;
import lombok.val;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.domain.UserVotes;
import org.cardano.foundation.voting.domain.VoteReceipt;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints.LOGIN_ENDPOINT;
import static org.cardano.foundation.voting.api.endpoints.VotingAppEndpoints.VOTE_ENDPOINT;
import static org.cardano.foundation.voting.resource.Headers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoteTests extends BaseTest {

    private final static String LOGIN_SIGNATURE = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a90a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840ad2bf6e1930a98848e586e957e108039d2cad34a778e9af45299e54a9cb84fe1c5a41700da7c2a7c041ff3ade25199454e62f314f57067d603885c9a809f0b01";
    private final static String LOGIN_PUBLIC_KEY = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";

    private static String getAccessToken() {
        return getAccessToken(null, null);
    }

    private static String getAccessToken(@Nullable String signature, @Nullable String publicKey) {
        /*
            Signed message:
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */
         val response = given()
                .header(X_Ballot_Signature, signature == null ? LOGIN_SIGNATURE : signature)
                .header(X_Ballot_PublicKey, publicKey == null ? LOGIN_PUBLIC_KEY : publicKey)
                .header(X_Ballot_Wallet_Type, WalletType.CARDANO.name())
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
                .header(X_Ballot_Signature, signature)
                .header(X_Ballot_PublicKey, publicKey)
                .header(X_Ballot_Wallet_Type, WalletType.CARDANO.name())
                .when().post(VOTE_ENDPOINT + "/cast")
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
                .when().get(VOTE_ENDPOINT + "/votes/" + eventId);

        assertEquals(200, votesResponse.getStatusCode());

        List<UserVotes> votes = votesResponse.jsonPath().getList(".", UserVotes.class);
        assertEquals(1, votes.size());
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
                "walletId": "stake_test1uzanmeujweq3cl4qxkfagxl0frahpk6eyck92faxv7mp9sst9nhwa",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
        */

        val signature = "84584aa3012704581de0bb3de79276411c7ea03593d41bef48fb70db59262c5527a667b612c26761646472657373581de0bb3de79276411c7ea03593d41bef48fb70db59262c5527a667b612c2a166686173686564f45901a90a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f7465737431757a616e6d65756a77657133636c3471786b666167786c3066726168706b366579636b393266617876376d7039737374396e687761222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840e664efd692ccd69d2edd8ed02eee44caeaa3b16029c30bc7148c9eb1e2766c36c6df696a439d8a9c09a3b3f85f0b21b0ce58bc5be115c396df46f169fbc9e106";
        val publicKey = "a5010102581de0bb3de79276411c7ea03593d41bef48fb70db59262c5527a667b612c203272006215820a8b173fc0a77be87598d394bc6cc80fbb92d8686c8ed733a13ae6f365ac45d23";

        // Get bearer token from login
        String accessToken = getAccessToken(signature, publicKey);
        // Get the votes
        Response votesResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(VOTE_ENDPOINT + "/votes/" + eventId);

        assertEquals(200, votesResponse.getStatusCode());

        List<UserVotes> votes = votesResponse.jsonPath().getList(".", UserVotes.class);
        assertEquals(0, votes.size());
    }

    @Test
    @Order(4)
    public void getVoteReceipt() {
        //testCastVote();
        /*
            Signed message:
            {
              "action": "VIEW_VOTE_RECEIPT",
              "actionText": "View Vote Receipt",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "category": "CHANGE_SOMETHING",
                "network": "PREPROD"
              }
            }
        */
        val publicKey = "a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d";
        val signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805";

        val response = given()
                .header(X_Ballot_Signature, signature)
                .header(X_Ballot_PublicKey, publicKey)
                .header(X_Ballot_Wallet_Type, WalletType.CARDANO.name())
                .when().get(VOTE_ENDPOINT + "/receipt");

        assertEquals(200, response.getStatusCode());

        val voteReceipt = response.jsonPath().getObject(".", VoteReceipt.class);
        Assertions.assertNotNull(voteReceipt);
        assertEquals(voteReceipt.getEvent(), eventId);
        assertEquals(voteReceipt.getCategory(), "CHANGE_SOMETHING");
        assertEquals(voteReceipt.getProposal(), "YES");
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
                .when().get(VOTE_ENDPOINT + "/receipt/" + eventId + "/" + category);

        assertEquals(200, response.getStatusCode());
        VoteReceipt voteReceipt = response.jsonPath().getObject(".", VoteReceipt.class);
        Assertions.assertNotNull(voteReceipt);
        assertEquals(voteReceipt.getEvent(), eventId);
        assertEquals(voteReceipt.getCategory(), "CHANGE_SOMETHING");
        assertEquals(voteReceipt.getProposal(), "YES");
    }

    @Test
    @Order(6)
    // TODO: Why is the jwt matcher for this route disabled? That's why the request ends up in a 403.
    public void testVoteChangingAvailable() {
        // Get bearer token from login
        String accessToken = getAccessToken();
        String voteId = "2658fb7d-cd12-48c3-bc95-23e73616b79f";

        given().header("Authorization", "Bearer " + accessToken)
                .when().get(VOTE_ENDPOINT + "/vote-changing-available/" + eventId + "/" + voteId)
                .then()
                .statusCode(403);
    }

}
