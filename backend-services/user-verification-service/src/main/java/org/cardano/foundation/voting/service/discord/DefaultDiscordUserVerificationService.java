package org.cardano.foundation.voting.service.discord;

import com.bloxbean.cardano.client.util.HexUtil;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.ChainFollowerClient.EventSummary;
import org.cardano.foundation.voting.client.KeriVerificationClient;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.WalletType;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationResponse;
import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.cardano.foundation.voting.repository.DiscordUserVerificationRepository;
import org.cardano.foundation.voting.service.common.VerificationResult;
import org.cardano.foundation.voting.utils.Addresses;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.MessageFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VerificationStatus.PENDING;
import static org.cardano.foundation.voting.domain.VerificationStatus.VERIFIED;
import static org.cardano.foundation.voting.domain.WalletType.CARDANO;
import static org.cardano.foundation.voting.domain.WalletType.KERI;
import static org.zalando.problem.Status.BAD_REQUEST;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultDiscordUserVerificationService implements DiscordUserVerificationService {

    private final ChainFollowerClient chainFollowerClient;

    private final KeriVerificationClient keriVerificationClient;

    private final DiscordUserVerificationRepository userVerificationRepository;

    private final Clock clock;

    private final ChainNetwork network;

    @Value("${validation.expiration.time.minutes}")
    private int validationExpirationTimeMinutes;

    @Override
    @Transactional
    public Either<Problem, DiscordStartVerificationResponse> startVerification(String eventId,
                                                                               DiscordStartVerificationRequest startVerificationRequest) {
        val discordIdHash = startVerificationRequest.getDiscordIdHash();

        val maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val eventDetailsE = chainFollowerClient.findEventById(eventId);
        if (eventDetailsE.isEmpty()) {
            log.error("event error:{}", eventDetailsE.getLeft());

            return Either.left(eventDetailsE.getLeft());
        }

        val eventM = eventDetailsE.get();
        if (eventM.isEmpty()) {
            log.warn("Event not found:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val event = eventM.orElseThrow();
        val createdAt = LocalDateTime.now(clock);
        val expiresAt = createdAt.plusMinutes(validationExpirationTimeMinutes);

        val discordUserVerification = DiscordUserVerification.builder()
                .discordIdHash(discordIdHash)
                .eventId(eventId)
                .secretCode(startVerificationRequest.getSecret())
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .status(PENDING)
                .build();

        if (event.finished()) {
            log.warn("Event already finished:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val saved = userVerificationRepository.saveAndFlush(discordUserVerification);

        return Either.right(DiscordStartVerificationResponse.builder()
                .eventId(eventId)
                .discordIdHash(saved.getDiscordIdHash())
                .status(saved.getStatus())
                .build()
        );
    }

    @Override
    @Transactional
    public Either<Problem, IsVerifiedResponse> checkVerification(DiscordCheckVerificationRequest request) {
        val eventId = request.getEventId();
        val walletId = request.getWalletId();
        val walletType = request.getWalletType();

        val eventValidationResultE = validateEvent(eventId);
        if (eventValidationResultE.isLeft()) {
            return Either.left(eventValidationResultE.getLeft());
        }

        val eventSummary = eventValidationResultE.get();
        if (!eventSummary.userBased() && walletType == KERI) {
            log.warn("Keri wallet not supported for BALANCE or STAKE type event");

            return Either.left(Problem.builder()
                    .withTitle("WALLET_NOT_SUPPORTED")
                    .withDetail("Keri wallet not supported for BALANCE or STAKE type event")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return switch (walletType) {
            case CARDANO -> handleCardanoVerification(request, eventId, walletType, walletId);
            case KERI -> handleKeriVerification(request, eventId, walletType, walletId);
        };
    }

    private Either<Problem, EventSummary> validateEvent(String eventId) {
        val eventDetailsResultE = chainFollowerClient.findEventById(eventId);

        if (eventDetailsResultE.isLeft()) {
            return Either.left(eventDetailsResultE.getLeft());
        }

        return eventDetailsResultE.get()
                .map(Either::<Problem, EventSummary>right)
                .orElseGet(() -> Either.left(Problem.builder()
                        .withTitle("EVENT_NOT_FOUND")
                        .withDetail("Event not found, eventId: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build()));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Either<Problem, IsVerifiedResponse> handleCardanoVerification(DiscordCheckVerificationRequest request,
                                                                          String eventId,
                                                                          WalletType walletType,
                                                                          String walletId) {
        val signatureM = request.getSignature();
        if (signatureM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val signature = signatureM.orElseThrow();

        val publicKeyM = request.getPublicKey();
        if (publicKeyM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_PUBLIC_KEY")
                    .withDetail("Missing public key.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val publicKey = publicKeyM.orElseThrow();

        // Verify signature specific to Cardano wallets
        val verificationResult = verifySignature(signature, publicKey);
        if (verificationResult.isLeft()) {
            return Either.left(verificationResult.getLeft());
        }

        val verificationData = verificationResult.get();
        val msg = verificationData.getMessage();
        val items = msg.split("\\|");

        if (items.length != 2) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, invalid signed message.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val discordIdHash = items[0];
        val cip30Secret = items[1];

        if (!request.getSecret().equals(cip30Secret)) {
            return Either.left(Problem.builder()
                    .withTitle("SECRET_MISMATCH")
                    .withDetail("Request Secret and CIP-30 secret mismatch.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val walletIdM = verificationData.getWalletId();
        if (walletIdM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, must have walletId in CIP-30 signature.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        if (!walletId.equals(walletIdM.get())) {
            return Either.left(Problem.builder()
                    .withTitle("WALLET_ID_MISMATCH")
                    .withDetail(String.format("Wallet id mismatch, walletId: %s, walletId: %s", walletId, walletIdM.get()))
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val walletIdCheck = Addresses.checkWalletId(network, walletType, walletId);
        if (walletIdCheck.isEmpty()) {
            return Either.left(walletIdCheck.getLeft());
        }

        val maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        val maybePendingVerification = userVerificationRepository.findPendingVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybePendingVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("NO_PENDING_VERIFICATION")
                    .withDetail("No pending verification found for discordIdHash:" + discordIdHash)
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        val pendingVerification = maybePendingVerification.get();

        val isSecretCodeMatch = pendingVerification.getSecretCode().equals(cip30Secret)
                && pendingVerification.getSecretCode().equals(request.getSecret());

        if (!isSecretCodeMatch) {
            return Either.left(Problem.builder()
                    .withTitle("AUTH_FAILED")
                    .withDetail("Invalid secret and / or discordIdHash.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val pendingUserVerification = maybePendingVerification.orElseThrow();

        val now = LocalDateTime.now(clock);

        val isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
        if (isCodeExpired) {
            return Either.left(Problem.builder()
                    .withTitle("VERIFICATION_EXPIRED")
                    .withDetail(String.format("Secret code: %s expired for walletId: %s and discordHashId:%s", cip30Secret, walletId, discordIdHash))
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .with("walletId", walletId)
                    .build());
        }

        pendingVerification.setWalletId(Optional.of(request.getWalletId()));
        pendingVerification.setWalletType(request.getWalletType());
        pendingVerification.setUpdatedAt(LocalDateTime.now(clock));
        pendingVerification.setStatus(VERIFIED);
        userVerificationRepository.save(pendingVerification);

        return Either.right(new IsVerifiedResponse(true));
    }

    private Either<Problem, VerificationResult> verifySignature(String signature,
                                                                String publicKey) {
        val verifier = new CIP30Verifier(signature, publicKey);
        val result = verifier.verify();

        if (!result.isValid()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val msg = result.getMessage(MessageFormat.TEXT);
        val maybeAddress = result.getAddress(AddressFormat.TEXT);

        return Either.right(new VerificationResult(msg, CARDANO, maybeAddress));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Either<Problem, IsVerifiedResponse> handleKeriVerification(DiscordCheckVerificationRequest request,
                                                                       String eventId,
                                                                       WalletType walletType,
                                                                       String walletId) {
        val signatureM = request.getSignature();
        if (signatureM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing KERI signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val signature = signatureM.orElseThrow();

        val payloadM = request.getPayload();
        if (payloadM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_PAYLOAD")
                    .withDetail("Missing KERI payload.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        val payload = new String(HexUtil.decodeHexString(payloadM.orElseThrow()));

        val oobiM = request.getOobi();
        if (oobiM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing KERI oobi.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val oobi = oobiM.orElseThrow();

        val items = payload.split("\\|");
        val discordIdHash = items[0];
        val secret = items[1];

        val maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        val maybePendingVerification = userVerificationRepository.findPendingVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybePendingVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("NO_PENDING_VERIFICATION")
                    .withDetail("No pending verification found for discordIdHash:" + discordIdHash)
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        val pendingVerification = maybePendingVerification.get();
        boolean isSecretCodeMatch = pendingVerification.getSecretCode().equals(secret)
                && pendingVerification.getSecretCode().equals(request.getSecret());

        if (!isSecretCodeMatch) {
            return Either.left(Problem.builder()
                    .withTitle("AUTH_FAILED")
                    .withDetail("Invalid secret and / or discordIdHash.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val pendingUserVerification = maybePendingVerification.orElseThrow();

        val now = LocalDateTime.now(clock);

        val isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
        if (isCodeExpired) {
            return Either.left(Problem.builder()
                    .withTitle("VERIFICATION_EXPIRED")
                    .withDetail(String.format("Secret code: %s expired for walletId: %s and discordHashId:%s", secret, walletId, discordIdHash))
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .with("walletId", walletId)
                    .build());
        }

        // Step 1: Check if OOBI is already registered
        Either<Problem, String> oobiCheckResult = keriVerificationClient.getOOBI(oobi, 1);
        log.info("oobiCheckResult: {}", oobiCheckResult.get());
        if (oobiCheckResult.isRight()) {
            log.info("OOBI already registered: {}", oobiCheckResult.get());
            Either<Problem, Boolean> verificationResult = keriVerificationClient.verifySignature(walletId, signature, payload);

            if (verificationResult.isLeft()) {
                return Either.left(verificationResult.getLeft());
            }

            if (!verificationResult.get()) {
                return Either.left(Problem.builder()
                        .withTitle("KERI_VERIFICATION_FAILED")
                        .withDetail("The Keri verification failed.")
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            log.info("Keri signature {} verified for walletId {} with payload {}", signatureM, walletId, payload);
            pendingVerification.setWalletId(Optional.of(walletId));
            pendingVerification.setWalletType(request.getWalletType());
            pendingVerification.setUpdatedAt(LocalDateTime.now(clock));
            pendingVerification.setStatus(VERIFIED);
            userVerificationRepository.save(pendingVerification);

            return Either.right(new IsVerifiedResponse(true));
        }

        log.info("OOBI not registered yet: {}", oobiM);
        // Step 2: Register OOBI if not already registered
        val oobiRegistrationResultE = keriVerificationClient.registerOOBI(oobi);

        if (oobiRegistrationResultE.isLeft()) {
            return Either.left(oobiRegistrationResultE.getLeft());
        }

        log.info("OOBI registered successfully: {}", oobiM);

        // Step 3: Attempt to verify OOBI registration up to 10 times
        val oobiFetchResultE = keriVerificationClient.getOOBI(oobi, 60);
        if (oobiFetchResultE.isLeft()) {
            return Either.left(oobiFetchResultE.getLeft());
        }

        // Step 4: Verify signature after OOBI registration
        val verificationResultE = keriVerificationClient.verifySignature(walletId, signature, payload);
        if (verificationResultE.isLeft()) {
            return Either.left(verificationResultE.getLeft());
        }

        if (!verificationResultE.get()) {
            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_FAILED")
                    .withDetail("The Keri verification failed.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        log.info("Keri signature {} verified for walletId {} with payload {}", signatureM, walletId, payload);
        pendingVerification.setWalletId(Optional.of(walletId));
        pendingVerification.setWalletType(request.getWalletType());
        pendingVerification.setUpdatedAt(LocalDateTime.now(clock));
        pendingVerification.setStatus(VERIFIED);
        userVerificationRepository.save(pendingVerification);

        return Either.right(new IsVerifiedResponse(true));
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerifiedBasedOnWalletId(IsVerifiedRequest isVerifiedRequest) {
        val eventId = isVerifiedRequest.getEventId();
        val walletType = isVerifiedRequest.getWalletType();
        val walletId = isVerifiedRequest.getWalletId();

        val isVerified = userVerificationRepository.findCompletedVerifications(eventId, walletType, walletId)
                .stream().findFirst()
                .map(uv -> new IsVerifiedResponse(true)).orElse(new IsVerifiedResponse(false));

        return Either.right(isVerified);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerifiedBasedOnDiscordIdHash(String eventId, String discordIdHash) {
        val isVerified = userVerificationRepository.findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash)
                .map(uv -> new IsVerifiedResponse(true)).orElse(new IsVerifiedResponse(false));

        return Either.right(isVerified);
    }

    @Override
    @Transactional
    public void removeUserVerification(DiscordUserVerification userVerification) {
        userVerificationRepository.delete(userVerification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscordUserVerification> findAllForEvent(String eventId) {
        return userVerificationRepository.findAllForEvent(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscordUserVerification> findAllPending(String eventId) {
        return userVerificationRepository.findAllPending(eventId);
    }

}
