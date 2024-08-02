package org.cardano.foundation.voting.api.tests;

import io.restassured.http.ContentType;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.UserVerificationEndpoints;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DiscordUserVerificationAPITests extends BaseTest {

    @Value("${discord.bot.username}")
    private String basicAuthUsername;

    @Value("${discord.bot.password}")
    private String basicAuthPassword;

    @Value("${discord.bot.eventId.binding}")
    private String discordBotEventId;

    @Value("${cardano.network}")
    private String cardanoNetwork;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Test
    public void testIsVerified() {
        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .pathParam("discordIdHash", "936a185caaa266bb9cbe981e9e05cb78cd732b0b3280eb944412bb6f8f8f07af")
                .when().get(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/is-verified/{discordIdHash}")
                .then()
                .statusCode(200)
                .body("verified", equalTo(false));


        // TODO: Update the service to send a 400 instead of a 404 in case of an empty discordIdHash
        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .pathParam("discordIdHash", "")
                .when().get(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/is-verified/{discordIdHash}")
                .then()
                .statusCode(404);
    }

    @Test void testStartVerification() {
        DiscordStartVerificationRequest startVerificationRequest = DiscordStartVerificationRequest.builder()
                .discordIdHash("07123e1f482356c415f684407a3b8723e10b2cbbc0b8fcd6282c49d37c9c1abc")
                .secret("chj3h3dtjq")
                .build();

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        // Update the secret and send with POST
        startVerificationRequest.setSecret("447zzqaztj");

        // TODO: Discuss if POST should be able to update or if we should use only PUT for that
        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        // Update the secret and send with PUT
        startVerificationRequest.setSecret("oax0islhx0j");

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().put(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        // Send an empty secret with PUT
        startVerificationRequest.setSecret("");

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().put(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(400);
    }

    @Test void testCheckVerification() {
        // Start a verification first
        DiscordStartVerificationRequest startVerificationRequest = DiscordStartVerificationRequest.builder()
                .discordIdHash("936a185caaa266bb9cbe981e9e05cb78cd732b0b3280eb944412bb6f8f8f07af")
                .secret("chj3h3dtjq")
                .build();

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        String signature = "84582aa201276761646472657373581de1820506cb0ce54ae755b2512b6cf31856d7265e8792cb" +
                "86afc94e0872a166686173686564f4584b39333661313835636161613236366262396362653938316539653035" +
                "6362373863643733326230623332383065623934343431326262366638663866303761667c63686a3368336474" +
                "6a715840a2440923efb7ead1c74b0a5ac3667e86335c76bccc12d2ae73fc6523da114d466b51436d24e89262382" +
                "a4f8b573b52de70104714a9de73946a7f24d1e0f58607";

        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String stakeAddress = "stake1uxpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsus20guat";

        DiscordCheckVerificationRequest discordCheckVerificationRequest = DiscordCheckVerificationRequest.builder()
                .eventId(discordBotEventId)
                .coseSignature(Optional.of(signature))
                .walletId(stakeAddress)
                .cosePublicKey(publicKey.describeConstable())
                .secret("chj3h3dtjq")
                .keriSignedMessage(Optional.empty())
                .keriPayload(Optional.empty())
                .oobi(Optional.empty())
                .build();

        int expectedStatusCode = 400;
        Boolean expectedVerified = null;
        if (cardanoNetwork.equalsIgnoreCase("MAIN")) {
            expectedStatusCode = 200;
            expectedVerified = true;
        }

        given().contentType(ContentType.JSON)
                .body(discordCheckVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                .then()
                .statusCode(expectedStatusCode)
                .body("verified", equalTo(expectedVerified));

        String testnetStakeAddress = "stake_test1uzpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsusd9z7ek";
        String testnetSignature = "84582aa201276761646472657373581de0820506cb0ce54ae755b2512b6cf31856d7265e8792cb8" +
                "6afc94e0872a166686173686564f4584b3933366131383563616161323636626239636265393831653965303563623738" +
                "63643733326230623332383065623934343431326262366638663866303761667c63686a33683364746a715840c0202dc" +
                "632249717ad55f3f45f0807b314a13472a6e80504446b89a1cdc687343cf7abba210af037402e93dde3ac7bbfc7db37afb" +
                "1fca8f1a82fd41c84f2a704";
        String testnetPublicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";

        expectedStatusCode = 200;
        expectedVerified = true;
        if (cardanoNetwork.equalsIgnoreCase("MAIN")) {
            expectedStatusCode = 400;
            expectedVerified = null;
        }

        DiscordCheckVerificationRequest discordCheckVerificationTestnetRequest = DiscordCheckVerificationRequest.builder()
                .eventId(discordBotEventId)
                .coseSignature(Optional.of(testnetSignature))
                .walletId(testnetStakeAddress)
                .cosePublicKey(testnetPublicKey.describeConstable())
                .secret("chj3h3dtjq")
                .keriSignedMessage(Optional.empty())
                .keriPayload(Optional.empty())
                .oobi(Optional.empty())
                .build();

        given().contentType(ContentType.JSON)
                .body(discordCheckVerificationTestnetRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                .then()
                .statusCode(expectedStatusCode)
                .body("verified", equalTo(expectedVerified));

        // Testnet or mainnet should not validate a signature from the other network
        if (cardanoNetwork.equalsIgnoreCase("MAIN")) {
            discordCheckVerificationRequest.setCoseSignature(Optional.of(testnetSignature));

            given().contentType(ContentType.JSON)
                    .body(discordCheckVerificationRequest)
                    .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                    .then()
                    .statusCode(400);
        } else {
            discordCheckVerificationTestnetRequest.setCoseSignature(Optional.of(signature));

            given().contentType(ContentType.JSON)
                    .body(discordCheckVerificationTestnetRequest)
                    .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                    .then()
                    .statusCode(400);
        }
    }

    @Test void testCheckVerificationWithInvalidSecret() {
        DiscordStartVerificationRequest startVerificationRequest = DiscordStartVerificationRequest.builder()
                .discordIdHash("e186022d0931afe9fe0690857e32f85e50165e7fbe0966d49609ef1981f920c6")
                .secret("49ayui27ue")
                .build();

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        String signature = "84582aa201276761646472657373581de1820506cb0ce54ae755b2512b6cf31856d7265e8792cb86" +
                "afc94e0872a166686173686564f4584b653138363032326430393331616665396665303639303835376533326638" +
                "356535303136356537666265303936366434393630396566313938316639323063367c34396179756932377565584" +
                "0c0e7b5a2ec40f0e732f98a57332ab813d9ec7aa9ca06345bdd3e2985df11bf356de64a3a61811aea621341ce4d77" +
                "027725f76140acbe486b39765b85b4a4cf07";

        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String stakeAddress = "stake1uxpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsus20guat";

        // secret changed 49ayui27ue to chj3h3dtjq
        DiscordCheckVerificationRequest discordCheckVerificationRequest = DiscordCheckVerificationRequest.builder()
                .eventId(discordBotEventId)
                .coseSignature(Optional.of(signature))
                .walletId(stakeAddress)
                .cosePublicKey(publicKey.describeConstable())
                .secret("chj3h3dtjq")
                .keriSignedMessage(Optional.empty())
                .keriPayload(Optional.empty())
                .oobi(Optional.empty())
                .build();

        given().contentType(ContentType.JSON)
                .body(discordCheckVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                .then()
                .statusCode(400);
    }

    @Test void testCheckVerificationWithNotMatchingDiscordId() {
        DiscordStartVerificationRequest startVerificationRequest = DiscordStartVerificationRequest.builder()
                .discordIdHash("e186022d0931afe9fe0690857e32f85e50165e7fbe0966d49609ef1981f920c6")
                .secret("49ayui27ue")
                .build();

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(startVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);

        // signature signed e56f8659317b265de26388da945ba5e196f74988bdfbad7416ebaba853692f1f|49ayui27ue instead of
        // e186022d0931afe9fe0690857e32f85e50165e7fbe0966d49609ef1981f920c6|49ayui27ue
        String signature = "84582aa201276761646472657373581de1820506cb0ce54ae755b2512b6cf31856d7265e8792cb86afc94e0" +
                "872a166686173686564f4584b6535366638363539333137623236356465323633383864613934356261356531393666373" +
                "43938386264666261643734313665626162613835333639326631667c3439617975693237756558404b6ea2454b6ad5266" +
                "00f8ff632334de15fbe6780d4e81a45c41fcdc5a71f66434b3f79f1e5cdd89c843168928ea41a49d1a7f40e5cf44f03da1" +
                "055aec897d501";

        String publicKey = "a4010103272006215820c9a521bd37b0a416ba404c120b1c8608745a56aff2530949c7893a5c3847d2fe";
        String stakeAddress = "stake1uxpq2pktpnj54e64kfgjkm8nrptdwfj7s7fvhp40e98qsus20guat";

        DiscordCheckVerificationRequest discordCheckVerificationRequest = DiscordCheckVerificationRequest.builder()
                .eventId(discordBotEventId)
                .coseSignature(Optional.of(signature))
                .walletId(stakeAddress)
                .cosePublicKey(publicKey.describeConstable())
                .secret("49ayui27ue")
                .keriSignedMessage(Optional.empty())
                .keriPayload(Optional.empty())
                .oobi(Optional.empty())
                .build();

        given().contentType(ContentType.JSON)
                .body(discordCheckVerificationRequest)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/check-verification")
                .then()
                .statusCode(400);
    }

}
