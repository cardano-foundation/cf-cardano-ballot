package org.cardano.foundation.voting.api.tests;
import io.restassured.http.ContentType;
import org.cardano.foundation.voting.api.BaseTest;
import org.cardano.foundation.voting.api.endpoints.UserVerificationEndpoints;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DiscordUserVerificationAPITests extends BaseTest {

    @Value("${discord.bot.username}")
    private String basicAuthUsername;

    @Value("${discord.bot.password}")
    private String basicAuthPassword;

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

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .pathParam("discordIdHash", "")
                .when().get(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/is-verified/{discordIdHash}")
                .then()
                .statusCode(400);
    }

    @Test void testStartVerification() {
        DiscordStartVerificationRequest startVerificationRequest = DiscordStartVerificationRequest.builder()
                .discordIdHash("07123e1f482356c415f684407a3b8723e10b2cbbc0b8fcd6282c49d37c9c1abc")
                .secret("chj3h3dtjq")
                .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("discordIdHash", "07123e1f482356c415f684407a3b8723e10b2cbbc0b8fcd6282c49d37c9c1abc");
        requestBody.put("secret", "chj3h3dtjq");

        given().auth()
                .preemptive()
                .basic(basicAuthUsername, basicAuthPassword)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post(UserVerificationEndpoints.DISCORD_USER_VERIFICATION_ENDPOINT + "/start-verification")
                .then()
                .statusCode(200);
    }

}
