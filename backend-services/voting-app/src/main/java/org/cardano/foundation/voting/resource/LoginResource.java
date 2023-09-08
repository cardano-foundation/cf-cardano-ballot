package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.auth.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class LoginResource {

    private final LoginService loginService;

    @RequestMapping(value = "/login", method = POST, produces = "application/json")
    @Timed(value = "resource.auth.login", histogram = true)
    public ResponseEntity<?> login(@RequestBody @Valid SignedWeb3Request loginRequest)  {
        return loginService.login(loginRequest)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        loginResult -> ResponseEntity.ok(loginResult)
                );
    }

}
