package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@Slf4j
public class KeriVerificationClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keri.ballot.verifier.base.url}")
    private String keriVerifierBaseUrl;

    public Either<Problem, Boolean> verifySignature(String aid, String signature, String payload) {
        String url = String.format("%s/verify", keriVerifierBaseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pre", aid);
        requestBody.put("signature", signature);
        requestBody.put("payload", payload);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Either.right(true);
            } else {
                return Either.left(Problem.builder()
                        .withTitle("KERI_VERIFICATION_FAILED")
                        .withDetail("The Keri-specific condition was not met.")
                        .withStatus(new HttpStatusAdapter(response.getStatusCode()))
                        .build());
            }
        } catch (HttpClientErrorException e) {

            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_ERROR")
                    .withDetail("Unable to verify signature, reason: " + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, Boolean> registerOOBI(String oobi) {
        String url = String.format("%s/oobi", keriVerifierBaseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("oobi", oobi);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Either.right(true);
            } else {
                return Either.left(Problem.builder()
                        .withTitle("OOBI_REGISTRATION_FAILED")
                        .withDetail("Failed to register OOBI.")
                        .withStatus(new HttpStatusAdapter(response.getStatusCode()))
                        .build());
            }
        } catch (HttpClientErrorException e) {
            return Either.left(Problem.builder()
                    .withTitle("OOBI_REGISTRATION_ERROR")
                    .withDetail("Unable to register OOBI, reason: " + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public Either<Problem, String> getOOBI(String oobi, Integer maxAttempts) {
        String url = String.format("%s/oobi?url=%s", keriVerifierBaseUrl, oobi);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int attempts = (maxAttempts == null) ? 1 : maxAttempts;
        int attempt = 0;

        while (attempt < attempts) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

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
