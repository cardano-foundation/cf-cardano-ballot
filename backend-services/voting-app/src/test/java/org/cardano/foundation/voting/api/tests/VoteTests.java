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

    private String getAccessToken(String signature, String publicKey) {
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
        val response = given()
                .header(X_Login_Signature, signature)
                .header(X_Login_PublicKey, publicKey)
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
        String signature = "84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45902b40a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a2022434153545f564f5445222c0a202020202020202020202020202022616374696f6e54657874223a20224361737420566f7465222c0a20202020202020202020202020202264617461223a207b0a20202020202020202020202020202020226964223a202232363538666237642d636431322d343863332d626339352d323365373336313662373966222c0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a202020202020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020202020202270726f706f73616c223a2022594553222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022766f7465644174223a20223430323632343036222c0a2020202020202020202020202020202022766f74696e67506f776572223a20223130343434353535363636220a20202020202020202020202020207d2c0a202020202020202020202020202022736c6f74223a20223430323632343036222c0a202020202020202020202020202022757269223a202268747470733a2f2f65766f74696e672e63617264616e6f2e6f72672f766f6c7461697265220a2020202020202020202020207d0a0a584069740e3d87ee22c6cc9e66db0cea6972c1007c9429346da890582d9435ef655fb127a1b9d4cb155b3bea6d8ccedbe810d53a4fd98c57f5dbdcb808c36ceee606";

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
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e" +
                "0872a166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f676" +
                "96e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f7465" +
                "737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a37733766766870343065393871737" +
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d58402a17cf6a88b6700c671d45ee67abdc863bea521425fef" +
                "dad6a6421a554ecdca9fd39f1acc713b35c655e8f8d519b50b7d80899e6d1af8733117da8c2f68a480d";

        // Get bearer token from login
        String accessToken = getAccessToken(signature, publicKey);
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
        String publicKey = "a5010102581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9f032720062158204c386f51120072d8b13f6fc7f382ce826303577f46d593c833be1186c5495ce7";
        String signature = "84584aa3012704581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9f6761646472657373581de0ff2dc7d366f261ad84cd90faf33ec3de965bd44b8a795d382d937c9fa166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f67696e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f746573743175726c6a6d33376e766d657872747679656b67303475653763303066766b3735667739386a686663396b6668653863347a72743679222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250524550524f44222c22726f6c65223a22564f544552227d7d5840ac25951188c5dd34204808e3ad0e404cf175abad672ffb643cbc928ee7e287bbfe40946970f1263e6efa3090e018ec01dc01a62bd717ab032b484f071d3f2401";

        // Get bearer token from login
        String accessToken = getAccessToken(signature, publicKey);
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
                "address": "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek",
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
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e" +
                "0872a166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f676" +
                "96e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f7465" +
                "737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a37733766766870343065393871737" +
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d58402a17cf6a88b6700c671d45ee67abdc863bea521425fef" +
                "dad6a6421a554ecdca9fd39f1acc713b35c655e8f8d519b50b7d80899e6d1af8733117da8c2f68a480d";

        // Get bearer token from login
        String accessToken = getAccessToken(signature, publicKey);
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
        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String signature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e" +
                "0872a166686173686564f458cd7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f676" +
                "96e222c22736c6f74223a223430323632343036222c2264617461223a7b2261646472657373223a227374616b655f7465" +
                "737431757a707132706b74706e6a35346536346b66676a6b6d386e7270746477666a37733766766870343065393871737" +
                "57364397a37656b222c226576656e74223a2243465f544553545f4556454e545f3031222c226e6574776f726b223a2250" +
                "524550524f44222c22726f6c65223a22564f544552227d7d58402a17cf6a88b6700c671d45ee67abdc863bea521425fef" +
                "dad6a6421a554ecdca9fd39f1acc713b35c655e8f8d519b50b7d80899e6d1af8733117da8c2f68a480d";

        // Get bearer token from login
        String accessToken = getAccessToken(signature, publicKey);
        String voteId = "2658fb7d-cd12-48c3-bc95-23e73616b79f";
        given().header("Authorization", "Bearer " + accessToken)
                .when().get(VotingAppEndpoints.VOTE_ENDPOINT + "/vote-changing-available/" + eventId + "/" + voteId)
                .then()
                .statusCode(403);
    }
}
