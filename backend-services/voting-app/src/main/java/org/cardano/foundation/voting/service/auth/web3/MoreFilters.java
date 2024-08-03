package org.cardano.foundation.voting.service.auth.web3;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.zalando.problem.Problem;

import java.io.IOException;

public final class MoreFilters {

    public static void sendBackProblem(ObjectMapper objectMapper,
                                        HttpServletResponse response,
                                        Problem problem) throws IOException {
        val statusCode = problem.getStatus().getStatusCode();

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().println(objectMapper.writeValueAsString(problem));
        response.getOutputStream().flush();
    }

}
