package org.cardano.foundation.voting.service.security;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Arrays;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.web3.Web3Action.CAST_VOTE;
import static org.cardano.foundation.voting.domain.web3.Web3Action.LOGIN;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.cardanofoundation.cip30.Format.TEXT;
import static org.cardanofoundation.cip30.ValidationError.UNKNOWN;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultLoginService implements LoginService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Autowired
    private CardanoNetwork network;

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

        var jsonPayloadE = jsonService.decodeCIP93LoginEnvelope(cip30VerificationResult.getMessage(TEXT));
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
        var slotStr = cip93LoginEnvelope.getSlot();

        if (!isNumeric(slotStr)) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("CIP-93 envelope slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        if (expirationService.isSlotExpired(Long.parseLong(slotStr))) {
            return Either.left(
                    Problem.builder()
                            .withTitle("EXPIRED_SLOT")
                            .withDetail("CIP-93 envelope slot is expired!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var actionText = cip93LoginEnvelope.getAction();
        var maybeAction = Enums.getIfPresent(Web3Action.class, actionText);
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

        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, cip93LoginEnvelope.getData().getNetwork());
        if (maybeNetwork.isEmpty()) {
            log.warn("Invalid network, network:{}", cip93LoginEnvelope.getData().getNetwork());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        String event = cip93LoginEnvelope.getData().getEvent();

        var maybeEvent = referenceDataService.findValidEventByName(event);
        if (maybeEvent.isEmpty()) {
            log.warn("Event not found, event:{}", event);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build());
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
        log.info("address:{}", new String(addressBytes));

        var stakeAddressE = Bech32.decode(addressBytes);
        if (stakeAddressE.isLeft()) {
            log.warn("Invalid bech32 address, addressBytes:{}", addressBytes);

            return Either.left(stakeAddressE.getLeft());
        }
        var stakeAddress = stakeAddressE.get();

        var passedStakeAddressE = stakeAddressVerificationService.checkStakeAddress(stakeAddress);
        if (passedStakeAddressE.isLeft()) {
            return Either.left(passedStakeAddressE.getLeft());
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

        return jwtService.generate(stakeAddress);
    }

}
