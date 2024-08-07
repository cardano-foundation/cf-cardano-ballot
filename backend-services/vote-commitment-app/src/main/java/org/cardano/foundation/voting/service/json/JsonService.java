package org.cardano.foundation.voting.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.domain.web3.KERIEnvelope;
import org.cardano.foundation.voting.domain.web3.VoteEnvelope;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
@RequiredArgsConstructor
public final class JsonService {

    private final ObjectMapper objectMapper;

    public Either<Problem, CIP93Envelope<VoteEnvelope>> decodeCIP93VoteEnvelope(String json) {
        try {
            return Either.right(objectMapper.readValue(json, new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            log.warn("Invalid json:{}", json, e);

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_JSON")
                            .withDetail("Invalid json:" + json)
                            .withStatus(BAD_REQUEST)
                            .withDetail(e.getMessage())
                            .build()
            );
        }
    }

    public Either<Problem, KERIEnvelope<VoteEnvelope>> decodeKERIVoteEnvelope(String json) {
        try {
            return Either.right(objectMapper.readValue(json, new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            log.warn("Invalid json:{}", json, e);

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_JSON")
                            .withDetail("Invalid json:" + json)
                            .withStatus(BAD_REQUEST)
                            .withDetail(e.getMessage())
                            .build()
            );
        }
    }

}
