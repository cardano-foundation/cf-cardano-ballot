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
        System.out.println("KeriVerificationClient");
        System.out.println("url");
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("aid", aid);
        requestBody.put("signature", signature);
        requestBody.put("payload", payload);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        System.out.println("entity");
        System.out.println(entity);
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
            System.out.println("HttpClientErrorException");
            System.out.println(e);

            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_ERROR")
                    .withDetail("Unable to verify signature, reason: " + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }
}
