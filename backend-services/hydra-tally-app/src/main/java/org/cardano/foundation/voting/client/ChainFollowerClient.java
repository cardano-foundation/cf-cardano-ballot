package org.cardano.foundation.voting.client;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TallyType;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Slf4j
public class ChainFollowerClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ledger.follower.app.base.url}")
    private String ledgerFollowerBaseUrl;

    public Either<Problem, Optional<EventDetailsResponse>> getEventDetails(String eventId) {
        var url = String.format("%s/api/reference/event/{id}", ledgerFollowerBaseUrl);

        try {
            return Either.right(Optional.ofNullable(restTemplate.getForObject(url, EventDetailsResponse.class, eventId)));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                return Either.right(Optional.empty());
            }

            return Either.left(Problem.builder()
                    .withTitle("REFERENCE_ERROR")
                    .withDetail("Unable to get event details from chain-tip follower service, reason:" + e.getMessage())
                    .withStatus(new HttpStatusAdapter(e.getStatusCode()))
                    .build());
        }
    }

    public record EventDetailsResponse(String id,
                                       String organisers,
                                       boolean proposalsReveal,
                                       VotingEventType votingEventType,
                                       List<Tally> tallies) {

        public Optional<Tally> findTallyByName(String name) {
            return tallies.stream()
                    .filter(t -> t.name().equals(name))
                    .findFirst();
        }

    }

    public record Tally (
            String name,
            String description,
            TallyType type,
            Object config) {
    }

    public record HydraTallyConfig(String compiledScript,
                                   String contractName,
                                   String compiledScriptHash,
                                   String compilerVersion,
                                   String compilerName,
                                   String plutusVersion,
                                   List<String> verificationKeys) {
    }

}
