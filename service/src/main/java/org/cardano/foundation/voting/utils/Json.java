package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Slf4j
public final class Json {

    public static Either<Problem, JsonNode> decode(String json) {
        try {
            var jsonNode = JsonUtil.parseJson(json);

            return Either.right(jsonNode);
        } catch (JsonProcessingException e) {
            log.warn("Invalid json:{}", json, e);

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_JSON")
                            .withDetail("Invalid json:" + json)
                            .withStatus(Status.BAD_REQUEST)
                            .withDetail(e.getMessage())
                            .build()
            );
        }
    }

}
