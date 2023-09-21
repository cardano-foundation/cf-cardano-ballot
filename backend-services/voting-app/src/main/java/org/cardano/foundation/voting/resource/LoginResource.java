package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.auth.LoginService;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.problem.Status.BAD_REQUEST;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class LoginResource {

    private final LoginService loginService;

    @RequestMapping(value = "/login", method = GET, produces = "application/json")
    @Timed(value = "resource.auth.login", histogram = true)
    public ResponseEntity<?> login(Authentication authentication)  {
        if (!(authentication instanceof Web3AuthenticationToken web3AuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .body(problem);
        }

        return loginService.login(web3AuthenticationToken)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        ResponseEntity::ok
                );
    }

}
