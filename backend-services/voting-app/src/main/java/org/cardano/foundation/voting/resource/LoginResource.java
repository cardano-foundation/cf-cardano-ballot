package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.service.auth.LoginService;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.springframework.http.CacheControl;
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
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class LoginResource {

    private final LoginService loginService;

    @RequestMapping(value = "/login", method = GET, produces = "application/json")
    @Timed(value = "resource.auth.login", histogram = true)
    @Operation(
            summary = "Log in using Web3 authentication token",
            description = "Authenticate user using CIP-93 auth headers tokens. This endpoint allows you to obtain a JWT auth token based on Web3 authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated and retrieved user details",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = LoginResult.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid authentication method. Only Web3 authentication is supported.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Problem.class))
                            }
                    )
            }
    )
    public ResponseEntity<?> login(Authentication authentication)  {
        var cacheControl = CacheControl.noCache()
                .noTransform()
                .mustRevalidate();

        if (!(authentication instanceof Web3AuthenticationToken web3AuthenticationToken)) {
            var problem = Problem.builder()
                    .withTitle("WEB3_AUTH_REQUIRED")
                    .withDetail("CIP-93 auth headers tokens needed!")
                    .withStatus(BAD_REQUEST)
                    .build();

            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .cacheControl(cacheControl)
                    .body(problem);
        }

        return loginService.login(web3AuthenticationToken)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        loginResult -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(loginResult);
                        }
                );
    }

}
