package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.auth.LoginService;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class LoginResource {

    private final LoginService loginService;

    @RequestMapping(value = "/login", method = GET, produces = "application/json")
    @Timed(value = "resource.auth.login", histogram = true)
    public ResponseEntity<?> login(Authentication authentication)  {
        return loginService.login((Web3AuthenticationToken) authentication)
                .fold(problem -> ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem),
                        ResponseEntity::ok
                );
    }

}
