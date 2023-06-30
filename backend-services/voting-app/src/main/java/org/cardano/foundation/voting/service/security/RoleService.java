package org.cardano.foundation.voting.service.security;

import com.bloxbean.cardano.client.account.Account;
import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Bech32;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.cardanofoundation.cip30.Format.TEXT;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
public class RoleService {

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Autowired
    private JsonService jsonService;

    public Either<Problem, Role> authoriseRole(SignedWeb3Request loginRequest) {
        var cip30Verifier = new CIP30Verifier(loginRequest.getCoseSignature(), Optional.ofNullable(loginRequest.getCosePublicKey()));

        var cip30VerificationResult = cip30Verifier.verify();
        if (!cip30VerificationResult.isValid()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                            .withDetail("CIP30 data signature verification failed")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var maybeCip93LoginEnvelope = jsonService.decodeCIP93LoginEnvelope(cip30VerificationResult.getMessage(TEXT));
        if (maybeCip93LoginEnvelope.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_CIP93_LOGIN_ENVELOPE")
                            .withDetail("Invalid CIP93 login envelope")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var loginEnvelopeCIP93Envelope = maybeCip93LoginEnvelope.get();
        var maybeWishedRole = Enums.getIfPresent(Role.class, loginEnvelopeCIP93Envelope.getData().getRole());
        if (maybeWishedRole.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("ROLE_NOT_FOUND")
                            .withDetail("Role not found!")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var wishedRole = maybeWishedRole.orElseThrow();
        if (wishedRole == Role.VOTER) {
            return Either.right(Role.VOTER);
        }

        var maybeAddress = cip30VerificationResult.getAddress();
        if (maybeAddress.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("ADDRESS_NOT_FOUND")
                            .withDetail("Address missing in the envelope")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }

        var bech32StakeAddressE = Bech32.decode(cip30VerificationResult.getAddress().orElseThrow());
        if (bech32StakeAddressE.isEmpty()) {
            return Either.left(
                    Problem.builder()
                            .withTitle("INVALID_ADDRESS")
                            .withDetail("Invalid bech32 address")
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
        var cip93LoginStakeAddress = bech32StakeAddressE.get();

        boolean isOrganiser = organiserAccount.stakeAddress().equals(cip93LoginStakeAddress);

        if (isOrganiser) {
            return Either.right(Role.ORGANISER);
        }

        return Either.left(
                Problem.builder()
                        .withTitle("UNAUTHORISED_ROLE")
                        .withDetail("Unauthorised role")
                        .withStatus(BAD_REQUEST)
                        .build()
        );
    }

}
