package org.cardano.foundation.voting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.domain.web3.LoginEnvelope;
import org.cardano.foundation.voting.domain.web3.VoteEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public final class JsonService {

    @Autowired
    private ObjectMapper objectMapper;

    public Either<Problem, CIP93Envelope<VoteEnvelope>> decodeCIP93VoteEnvelope(String json) {
        try {
            var jsonNode = objectMapper.readValue(json, new TypeReference<CIP93Envelope<VoteEnvelope>>(){});

            return Either.right(jsonNode);
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
            var jsonNode = objectMapper.readValue(json, new TypeReference<CIP93Envelope<LoginEnvelope>>(){});

            return Either.right(jsonNode);
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
