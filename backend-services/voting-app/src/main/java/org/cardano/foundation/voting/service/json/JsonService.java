package org.cardano.foundation.voting.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public final class JsonService {

    @Autowired
    private ObjectMapper objectMapper;

    public Either<Problem, JsonNode> decode(String json) {
        try {
            return Either.right(objectMapper.readTree(json));
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

    public Either<Problem, CIP93Envelope<VoteEnvelope>> decodeCIP93VoteEnvelope(String json) {
        try {
            System.out.println(json);
            return Either.right(objectMapper.readValue(json, new TypeReference<CIP93Envelope<VoteEnvelope>>() { }));
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

    public Either<Problem, CIP93Envelope<LoginEnvelope>> decodeCIP93LoginEnvelope(String json) {
        try {
            return Either.right(objectMapper.readValue(json, new TypeReference<>() { }));
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

    public Either<Problem, CategoryRegistrationEnvelope> decodeCategoryRegistrationEnvelope(String json) {
        try {
            return Either.right(objectMapper.readValue(json, new TypeReference<>() {}));
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

    public Either<Problem, EventRegistrationEnvelope> decodeEventRegistrationEnvelope(String json) {
        try {
            return Either.right(objectMapper.readValue(json, new TypeReference<>() {}));
        } catch (JsonProcessingException  e) {
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
