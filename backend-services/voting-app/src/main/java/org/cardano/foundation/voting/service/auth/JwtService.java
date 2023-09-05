package org.cardano.foundation.voting.service.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.text.ParseException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.nimbusds.jose.JWSAlgorithm.EdDSA;
import static org.cardano.foundation.voting.utils.MoreTime.convertToDateViaInstant;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
public class JwtService {

    @Autowired
    private OctetKeyPair cfJWTKey;

    @Autowired
    private Clock clock;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Value("${cardano.jwt.iss}")
    private String iss;

    @Value("${cardano.jwt.tokenValidityDurationHours}")
    private long tokenValidityDurationHours;

    @Timed(value = "service.jwt.generate", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, LoginResult> generate(String stakeAddress,
                                                 String eventId,
                                                 Role role) {
        var now = LocalDateTime.now(clock);
        try {
            var signer = new Ed25519Signer(cfJWTKey);
            var expirationLocalDateTime = now.plusHours(tokenValidityDurationHours);
            var legacyExpirationDate = convertToDateViaInstant(expirationLocalDateTime);

            var claimsSet = new JWTClaimsSet.Builder()
                    .subject(stakeAddress)
                    .jwtID(UUID.randomUUID().toString())
                    .issuer(iss)
                    .claim("stakeAddress", stakeAddress)
                    .claim("eventId", eventId)
                    .claim("role", role)
                    .claim("cardanoNetwork", cardanoNetwork.name())
                    .issueTime(convertToDateViaInstant(now))
                    .expirationTime(legacyExpirationDate)
                    .build();

            var header = new JWSHeader.Builder(EdDSA)
                    .keyID(cfJWTKey.getKeyID())
                    .build();

            var signedJWT = new SignedJWT(header, claimsSet);

            signedJWT.sign(signer);

            var accessToken = signedJWT.serialize();

            return Either.right(new LoginResult(accessToken, expirationLocalDateTime));
        } catch (JOSEException e) {
            log.error("JWT token generation error", e);

            return Either.left(Problem.builder()
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .withTitle("JWT_GENERATION_FAILED")
                    .withDetail(e.getMessage())
                    .build());
        }

    }

    @Timed(value = "service.jwt.verify", percentiles = { 0.3, 0.5, 0.95 })
    public Either<Problem, SignedJWT> verify(String token) {
        var publicJWK = cfJWTKey.toPublicJWK();

        try {
            var verifier = new Ed25519Verifier(publicJWK);
            var signedJWT = SignedJWT.parse(token);

            if (signedJWT.verify(verifier)) {
                var jwtClaimsSet = signedJWT.getJWTClaimsSet();
                var sub = jwtClaimsSet.getSubject();

                var issuerCheck = jwtClaimsSet.getIssuer().equals(iss);

                if (!issuerCheck) {
                    log.warn("JWT token verification failed for token:{}, issuer check failed.", token);

                    return Either.left(
                            Problem.builder()
                                    .withTitle("JWT_ISSUER_MISMATCH")
                                    .withDetail("JWT verification failed for token:" + token + " due to issuer check failed.")
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                var jwtCardanoNetwork = jwtClaimsSet.getStringClaim("cardanoNetwork");
                var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, jwtCardanoNetwork);
                if (maybeNetwork.isEmpty()) {
                    log.warn("Invalid jwtNetwork, jwtNetwork:{}", jwtCardanoNetwork);

                    return Either.left(Problem.builder()
                            .withTitle("INVALID_NETWORK")
                            .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                            .withStatus(BAD_REQUEST)
                            .build());
                }

                var jwtNetwork = maybeNetwork.orElseThrow();

                if (jwtNetwork != cardanoNetwork) {
                    log.warn("Network mismatch, network:{}", jwtNetwork);

                    return Either.left(Problem.builder()
                            .withTitle("NETWORK_MISMATCH")
                            .withDetail("Invalid network, backend configured with betwork:" + cardanoNetwork + ", however request is with network:" + jwtNetwork)
                            .withStatus(BAD_REQUEST)
                            .build());

                }

                var jwtStakeAddress = jwtClaimsSet.getStringClaim("stakeAddress");

                var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(jwtStakeAddress);
                if (stakeAddressCheckE.isLeft()) {
                    return Either.left(stakeAddressCheckE.getLeft());
                }

                var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(jwtStakeAddress);
                if (stakeAddressNetworkCheck.isLeft()) {
                    return Either.left(stakeAddressNetworkCheck.getLeft());
                }

                log.info("Verified sub:{}, stakeAddress:{}, ", sub, jwtStakeAddress);

                return Either.right(signedJWT);
            }

            log.error("JWT token verification failed for token:{}, signature verification failed.", token);

            return Either.left(
                    Problem.builder()
                            .withTitle("JWT_VERIFICATION_FAILED")
                            .withDetail("JWT verification failed for token:" + token)
                            .withStatus(UNAUTHORIZED)
                            .build()
            );
        } catch (JOSEException e) {
            log.warn("JWT token verification error", e);

            return Either.left(
                    Problem.builder()
                            .withTitle("JWT_VERIFICATION_FAILED")
                            .withDetail("JWT verification failed for token:" + token)
                            .withStatus(INTERNAL_SERVER_ERROR)
                            .build()
            );
        } catch (ParseException e) {
            log.warn("JWT token parse error", e);

            return Either.left(
                    Problem.builder()
                            .withTitle("JWT_VERIFICATION_FAILED")
                            .withDetail("JWT verification failed for token:" + token)
                            .withStatus(INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }

}