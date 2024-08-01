package org.cardano.foundation.voting.service.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.LoginResult;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.utils.Addresses;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.text.ParseException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.nimbusds.jose.JWSAlgorithm.EdDSA;
import static org.cardano.foundation.voting.utils.MoreTime.convertToDateViaInstant;
import static org.zalando.problem.Status.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class JwtService {

    private final OctetKeyPair cfJWTKey;

    private final Clock clock;

    private final ChainNetwork chainNetworkStartedOn;

    @Value("${cardano.jwt.iss}")
    private String iss;

    @Value("${cardano.jwt.tokenValidityDurationHours}")
    private long tokenValidityDurationHours;

    @Timed(value = "service.jwt.generate", histogram = true)
    public Either<Problem, LoginResult> generate(WalletType walletType,
                                                 String walletId,
                                                 String eventId,
                                                 Role role) {
        val now = LocalDateTime.now(clock);

        try {
            val signer = new Ed25519Signer(cfJWTKey);
            val expirationLocalDateTime = now.plusHours(tokenValidityDurationHours);
            val legacyExpirationDate = convertToDateViaInstant(expirationLocalDateTime);

            val claimsSet = new JWTClaimsSet.Builder()
                    .subject(walletId)
                    .jwtID(UUID.randomUUID().toString())
                    .issuer(iss)
                    .claim("walletType", walletType.name())
                    .claim("walletId", walletId)
                    .claim("eventId", eventId)
                    .claim("role", role)
                    .claim("network", chainNetworkStartedOn.name())
                    .issueTime(convertToDateViaInstant(now))
                    .expirationTime(legacyExpirationDate)
                    .build();

            val header = new JWSHeader.Builder(EdDSA)
                    .keyID(cfJWTKey.getKeyID())
                    .build();

            val signedJWT = new SignedJWT(header, claimsSet);

            signedJWT.sign(signer);

            val accessToken = signedJWT.serialize();

            return Either.right(new LoginResult(accessToken, walletType, expirationLocalDateTime));
        } catch (JOSEException e) {
            log.error("JWT token generation error", e);

            return Either.left(Problem.builder()
                    .withTitle("JWT_GENERATION_FAILED")
                    .withDetail(e.getMessage())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @Timed(value = "service.jwt.verify", histogram = true)
    public Either<Problem, SignedJWT> verify(String token) {
        val publicJWK = cfJWTKey.toPublicJWK();

        try {
            val verifier = new Ed25519Verifier(publicJWK);
            val signedJWT = SignedJWT.parse(token);

            if (signedJWT.verify(verifier)) {
                val jwtClaimsSet = signedJWT.getJWTClaimsSet();
                val sub = jwtClaimsSet.getSubject();

                val issuerCheck = jwtClaimsSet.getIssuer().equals(iss);

                if (!issuerCheck) {
                    log.warn("JWT token verification failed for token:{}, issuer check failed.", token);

                    return Either.left(
                            Problem.builder()
                                    .withTitle("JWT_ISSUER_MISMATCH")
                                    .withDetail("JWT verification failed for token:" + token + " due to issuer check failed.")
                                    .withStatus(BAD_REQUEST)
                                    .build()
                    );
                }

                val now = LocalDateTime.now(clock);
                val nowDate = convertToDateViaInstant(now);
                val expDate = signedJWT.getJWTClaimsSet().getExpirationTime();

                if (nowDate.after(expDate)) {
                    log.info("JWT token verification failed for token, token expired on: {}", expDate);

                    return Either.left(
                            Problem.builder()
                                    .withTitle("JWT_EXPIRED")
                                    .withDetail("JWT verification failed for token, token expired on: " + expDate)
                                    .withStatus(BAD_REQUEST)
                                    .build());
                }

                val jwtChainNetwork = jwtClaimsSet.getStringClaim("network");
                val networkM = Enums.getIfPresent(ChainNetwork.class, jwtChainNetwork);
                if (networkM.isEmpty()) {
                    log.warn("Invalid network, network:{}", jwtChainNetwork);

                    return Either.left(Problem.builder()
                            .withTitle("INVALID_NETWORK")
                            .withDetail("Invalid network, supported networks: " + ChainNetwork.supportedNetworks())
                            .withStatus(BAD_REQUEST)
                            .build());
                }

                val jwtNetwork = networkM.orElseThrow();
                if (jwtNetwork != chainNetworkStartedOn) {
                    log.warn("Network mismatch, network:{}", jwtNetwork);

                    return Either.left(Problem.builder()
                            .withTitle("NETWORK_MISMATCH")
                            .withDetail("Invalid network, backend configured with network: " + chainNetworkStartedOn + ", however request is with network: " + jwtNetwork)
                            .withStatus(BAD_REQUEST)
                            .build());

                }

                val walletId = jwtClaimsSet.getStringClaim("walletId");
                val walletTypeM = Enums.getIfPresent(WalletType.class, jwtClaimsSet.getStringClaim("walletType"));

                if (walletTypeM.isEmpty()) {
                    log.warn("Invalid wallet type, walletType:{}", jwtClaimsSet.getStringClaim("walletType"));

                    return Either.left(Problem.builder()
                            .withTitle("INVALID_WALLET_TYPE")
                            .withDetail("Invalid wallet type, supported wallet types:" + Arrays.asList(WalletType.values()))
                            .withStatus(BAD_REQUEST)
                            .build());
                }

                val walletType = walletTypeM.orElseThrow();
                val walletIdCheck = Addresses.checkWalletId(chainNetworkStartedOn, walletType, walletId);
                if (walletIdCheck.isEmpty()) {
                    return Either.left(walletIdCheck.getLeft());
                }

                val roleM = Enums.getIfPresent(Role.class, jwtClaimsSet.getStringClaim("role"));
                if (roleM.isEmpty()) {
                    log.warn("Invalid role, role:{}", jwtClaimsSet.getStringClaim("role"));

                    return Either.left(Problem.builder()
                            .withTitle("INVALID_ROLE")
                            .withDetail("Invalid role, supported roles:" + Role.supportedRoles())
                            .withStatus(BAD_REQUEST)
                            .build());
                }

                log.info("Verified sub:{}, ", sub);

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
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        } catch (ParseException e) {
            log.warn("JWT token parse error, reason:{}", e.getMessage());

            return Either.left(
                    Problem.builder()
                            .withTitle("JWT_VERIFICATION_FAILED")
                            .withDetail("JWT verification failed for token:" + token)
                            .withStatus(BAD_REQUEST)
                            .build()
            );
        }
    }

}
