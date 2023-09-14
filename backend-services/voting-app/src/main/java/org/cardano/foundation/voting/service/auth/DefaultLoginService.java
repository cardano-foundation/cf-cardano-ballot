package org.cardano.foundation.voting.service.auth;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.service.auth.jwt.JwtService;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Arrays;

import static org.cardano.foundation.voting.domain.web3.Web3Action.LOGIN;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultLoginService implements LoginService {

    private final JwtService jwtService;

    private final JsonService jsonService;

    @Override
    @Timed(value = "service.auth.login", histogram = true)
    public Either<Problem, LoginResult> login(Web3AuthenticationToken web3AuthenticationToken) {
        var details = web3AuthenticationToken.getDetails();

        String eventId = details.getEvent().id();
        String stakeAddress = details.getStakeAddress();

        var cip30VerificationResult = details.getCip30VerificationResult();
        var jsonBody = cip30VerificationResult.getMessage(TEXT);

        var jsonPayloadE = jsonService.decodeCIP93LoginEnvelope(jsonBody);
        if (jsonPayloadE.isLeft()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid login signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var cip93LoginEnvelope = jsonPayloadE.get();

        if (details.getAction() != LOGIN) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not LOGIN, expected action:" + LOGIN.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeRole = Enums.getIfPresent(Role.class, cip93LoginEnvelope.getData().getRole());
        if (maybeRole.isEmpty()) {
            log.warn("Invalid role, role:{}", cip93LoginEnvelope.getData().getRole());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_ROLE")
                    .withDetail("Invalid role, supported roles:" + Arrays.asList(Role.values()))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var role = maybeRole.orElseThrow();

        if (role != Role.VOTER) {
            return Either.left(Problem.builder()
                    .withTitle("ROLE_NOT_SUPPORTED")
                    .withDetail("Only VOTER role is supported for login for now!")
                    .withStatus(BAD_REQUEST)
                    .build(
            ));
        }

        return jwtService.generate(stakeAddress, eventId, role);
    }

}
