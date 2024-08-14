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

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    public Either<Problem, Boolean> registerOOBI(String oobi) {
        val url = String.format("%s/oobi", keriVerifierBaseUrl);

        val headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        val requestBody = new HashMap<String, String>();
        requestBody.put("oobi", oobi);

        val entity = new HttpEntity<Map<String, String>>(requestBody, headers);
        try {
            val response = restTemplate.exchange(url, POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Either.right(true);
            }

            return Either.left(Problem.builder()
                    .withTitle("OOBI_REGISTRATION_FAILED")
                    .withDetail("Failed to register OOBI.")
                    .withStatus(new HttpStatusAdapter(response.getStatusCode()))
                    .build());
        } catch (HttpClientErrorException e) {
            return Either.left(Problem.builder()
                    .withTitle("OOBI_REGISTRATION_ERROR")
                    .withDetail("Unable to register OOBI, reason: " + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, String> getOOBI(String oobi, Integer maxAttempts) {
        val url = String.format("%s/oobi?url=%s", keriVerifierBaseUrl, oobi);

        val headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        val entity = new HttpEntity<Void>(headers);

        int attempts = (maxAttempts == null) ? 1 : maxAttempts;
        int attempt = 0;

        while (attempt < attempts) {
            try {
                val response = restTemplate.exchange(url, GET, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    return Either.right(response.getBody());
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() != BAD_REQUEST) {
                    return Either.left(Problem.builder()
                            .withTitle("OOBI_FETCH_ERROR")
                            .withDetail("Unable to fetch OOBI, reason: " + e.getMessage())
                            .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                            .build());
                }
            }

            attempt++;
            if (attempt < attempts) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return Either.left(Problem.builder()
                            .withTitle("INTERRUPTED_ERROR")
                            .withDetail("Thread was interrupted while waiting to retry.")
                            .withStatus(new HttpStatusAdapter(BAD_REQUEST))
                            .build());
                }
            }
        }

        return Either.left(Problem.builder()
                .withTitle("OOBI_NOT_FOUND")
                .withDetail("The OOBI was not found after " + attempts + " attempts.")
                .withStatus(new HttpStatusAdapter(BAD_REQUEST))
                .build());
    }

}
