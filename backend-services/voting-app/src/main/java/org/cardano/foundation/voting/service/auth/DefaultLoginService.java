package org.cardano.foundation.voting.service.auth;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.LoginEnvelope;
import org.cardano.foundation.voting.service.auth.jwt.JwtService;
import org.cardano.foundation.voting.service.auth.web3.Web3AuthenticationToken;
import org.cardano.foundation.voting.service.auth.web3.Web3ConcreteDetails;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Arrays;

import static org.cardano.foundation.voting.domain.web3.Web3Action.LOGIN;
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
        val concreteDetails = web3AuthenticationToken.getDetails();
        val commonDetails = concreteDetails.getWeb3CommonDetails();

        val eventId = commonDetails.getEvent().id();
        val walletType = commonDetails.getWalletType();
        val walletId = commonDetails.getWalletId();

        if (commonDetails.getAction() != LOGIN) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not LOGIN, expected action:" + LOGIN.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val loginEnvelopeE = unwrapLoginVoteEnvelope(concreteDetails);
        if (loginEnvelopeE.isLeft()) {
            return Either.left(loginEnvelopeE.getLeft());
        }
        val loginEnvelope = loginEnvelopeE.get();

        var roleM = Enums.getIfPresent(Role.class, loginEnvelope.getRole());
        if (roleM.isEmpty()) {
            log.warn("Invalid role, role:{}", loginEnvelope.getRole());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_ROLE")
                    .withDetail("Invalid role, supported roles:" + Arrays.asList(Role.values()))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var role = roleM.orElseThrow();

        if (role != Role.VOTER) {
            return Either.left(Problem.builder()
                    .withTitle("ROLE_NOT_SUPPORTED")
                    .withDetail("Only VOTER role is supported for login for now!")
                    .withStatus(BAD_REQUEST)
                    .build(
                    ));
        }

        return jwtService.generate(walletType, walletId, eventId, role);
    }

    private Either<Problem, LoginEnvelope> unwrapLoginVoteEnvelope(Web3ConcreteDetails concreteDetails) {
        val jsonBody = concreteDetails.getPayload();

        val jsonPayloadE = jsonService.decodeCIP93LoginEnvelope(jsonBody);
        if (jsonPayloadE.isLeft()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("ENVELOPE_UNWRAP_ERROR")
                            .withDetail("Invalid login signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        return Either.right(jsonPayloadE.get().getData());
    }

}