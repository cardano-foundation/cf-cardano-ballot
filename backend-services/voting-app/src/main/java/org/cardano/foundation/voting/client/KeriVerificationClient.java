package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@Component
@Slf4j
public class KeriVerificationClient {

    private final RestTemplate restTemplate;

    @Value("${keri.ballot.verifier.base.url}")
    private String keriVerifierBaseUrl;

    public Either<Problem, Boolean> verifySignature(String aid,
                                                    String signature,
                                                    String payload) {
        val url = String.format("%s/verify", keriVerifierBaseUrl);

        val headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        val requestBody = new HashMap<String, String>();
        requestBody.put("pre", aid);
        requestBody.put("signature", signature);
        requestBody.put("payload", payload);

        val entity = new HttpEntity<Map<String, String>>(requestBody, headers);

        try {
            val response = restTemplate.exchange(url, POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Either.right(true);
            }

            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_FAILED")
                    .withDetail("The Keri-specific condition was not met.")
                    .withStatus(new HttpStatusAdapter(response.getStatusCode()))
                    .build());
        } catch (HttpClientErrorException e) {
            log.error("Unable to verify signature, reason: {}", e.getMessage());

            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_ERROR")
                    .withDetail("Unable to verify signature, reason: " + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

}
