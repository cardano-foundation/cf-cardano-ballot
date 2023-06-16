package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.request.LoginSignedWeb3Request;
import org.cardano.foundation.voting.service.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class LoginResource {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = POST, produces = "application/json")
    @Timed(value = "resource.auth.login", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> login(@RequestBody LoginSignedWeb3Request loginRequest)  {
        return loginService.login(loginRequest)
                .fold(problem -> {
                        return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                },
                        response -> ResponseEntity.ok(Map.of("accessToken", response))
                );
    }

}
