package org.cardano.foundation.voting.service.security;

import com.google.common.base.Enums;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.service.blockchain_state.SlotService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Json;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.cardano.foundation.voting.domain.web3.Web3Action.CAST_VOTE;
import static org.cardano.foundation.voting.domain.web3.Web3Action.LOGIN;
import static org.cardanofoundation.cip30.Format.TEXT;
import static org.cardanofoundation.cip30.ValidationError.UNKNOWN;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultLoginService implements LoginService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SlotService slotService;

    @Override
    @Timed(value = "service.auth.login", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, String> login(SignedWeb3Request loginRequest) {
        var cip30Verifier = new CIP30Verifier(loginRequest.getCoseSignature(), Optional.ofNullable(loginRequest.getCosePublicKey()));

        var cip30VerificationResult = cip30Verifier.verify();
        if (!cip30VerificationResult.isValid()) {
            var validationError = cip30VerificationResult.getValidationError().orElse(UNKNOWN);
            log.warn("CIP30 data sign for casting vote verification failed, validationError:{}", validationError);

            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid login signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var jsonPayload = cip30VerificationResult.getMessage(TEXT);
        var jsonPayloadE = Json.decode(jsonPayload);
        if (jsonPayloadE.isLeft()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("Invalid login signature!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var jsonPayloadNode = jsonPayloadE.get();
        var slot = jsonPayloadNode.get("request").get("slot").asLong();

        if (slotService.isSlotExpired(slot)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("Login's envelope slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var actionText = jsonPayloadNode.get("action").asText();
        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText).toJavaUtil();
        if (maybeAction.isEmpty()) {
            log.warn("Unknown action, action:{}", actionText);

            return Either.left(Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found, expected action:" + CAST_VOTE.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var action = maybeAction.orElseThrow();
        if (action != LOGIN) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_ACTION")
                    .withDetail("Action is not LOGIN, expected action:" + LOGIN.name())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeAddress = cip30VerificationResult.getAddress();
        if (maybeAddress.isEmpty()) {
            log.warn("Address not found in the signed data");

            return Either.left(
                    Problem.builder()
                            .withTitle("ADDRESS_NOT_FOUND")
                            .withDetail("Bech32 address not found in the signed data.")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var addressBytes = maybeAddress.orElseThrow();

        var stakeAddressE = Bech32.decode(addressBytes);
        if (stakeAddressE.isLeft()) {
            log.warn("Invalid bech32 address, addressBytes:{}", addressBytes);

            return Either.left(stakeAddressE.getLeft());
        }

        var stakeAddress = stakeAddressE.get();

        return jwtService.generate(stakeAddress);
    }

}
