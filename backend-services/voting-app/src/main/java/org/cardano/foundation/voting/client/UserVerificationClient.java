package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

@Component
@Slf4j
public class UserVerificationClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.verification.app.base.url}")
    private String userVerificationBaseUrl;

    public Either<Problem, IsVerifiedResponse> isVerified(String eventId, String stakeAddress) {
        var url = String.format("%s/api/user-verification/verified/{eventId}/{stakeAddress}", userVerificationBaseUrl);

        try {
            return Either.right(restTemplate.getForObject(url, IsVerifiedResponse.class, eventId, stakeAddress));
        } catch (HttpClientErrorException e) {
            return Either.left(Problem.builder()
                    .withTitle("VERIFICATION_ERROR")
                    .withDetail("Unable to get verification status from user-verification service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public record IsVerifiedResponse(boolean verified) {

        public boolean isNotYetVerified() {
            return !verified;
        }

    }

}
