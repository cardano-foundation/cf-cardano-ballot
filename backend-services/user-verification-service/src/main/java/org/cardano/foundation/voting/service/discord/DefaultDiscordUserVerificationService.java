package org.cardano.foundation.voting.service.discord;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.KeriVerificationClient;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.service.common.VerificationResult;
import org.cardano.foundation.voting.domain.discord.DiscordCheckVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationRequest;
import org.cardano.foundation.voting.domain.discord.DiscordStartVerificationResponse;
import org.cardano.foundation.voting.domain.entity.DiscordUserVerification;
import org.cardano.foundation.voting.repository.DiscordUserVerificationRepository;
import org.cardano.foundation.voting.utils.StakeAddress;
import org.cardano.foundation.voting.utils.WalletType;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.MessageFormat;
import org.cardano.foundation.voting.client.ChainFollowerClient.EventSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.zalando.problem.Problem;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VerificationStatus.PENDING;
import static org.cardano.foundation.voting.domain.VerificationStatus.VERIFIED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultDiscordUserVerificationService implements DiscordUserVerificationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private KeriVerificationClient keriVerificationClient;

    @Autowired
    private DiscordUserVerificationRepository userVerificationRepository;

    @Autowired
    private Clock clock;

    @Value("${validation.expiration.time.minutes}")
    private int validationExpirationTimeMinutes;

    @Autowired
    private CardanoNetwork network;

    @Override
    @Transactional
    public Either<Problem, DiscordStartVerificationResponse> startVerification(String eventId, DiscordStartVerificationRequest startVerificationRequest) {
        var discordIdHash = startVerificationRequest.getDiscordIdHash();

        var maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var eventDetails = chainFollowerClient.findEventById(eventId);

        if (eventDetails.isEmpty()) {
            log.error("event error:{}", eventDetails.getLeft());

            return Either.left(eventDetails.getLeft());
        }

        var maybeEvent = eventDetails.get();
        if (maybeEvent.isEmpty()) {
            log.warn("Event not found:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();
        var createdAt = LocalDateTime.now(clock);
        var expiresAt = createdAt.plusMinutes(validationExpirationTimeMinutes);

        var discordUserVerification = DiscordUserVerification.builder()
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

        var saved = userVerificationRepository.saveAndFlush(discordUserVerification);

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
        String eventId = request.getEventId();
        String walletId = request.getWalletId();
        Optional<WalletType> walletType = request.getWalletType();

        if (!walletType.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_WALLET_TYPE")
                    .withDetail("Wallet type must be specified.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        Either<Problem, EventSummary> eventValidationResult = validateEvent(eventId);
        if (eventValidationResult.isLeft()) {
            return Either.left(eventValidationResult.getLeft());
        }

        EventSummary eventSummary = eventValidationResult.get();

        if (!eventSummary.userBased() && walletType.get() == WalletType.KERI) {
            log.warn("Keri wallet not supported for BALANCE or STAKE type event");

            return Either.left(Problem.builder()
                    .withTitle("WALLET_NOT_SUPPORTED")
                    .withDetail("Keri wallet not supported for BALANCE or STAKE type event")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        switch (walletType.get()) {
            case CARDANO:
                return handleCardanoVerification(request, eventId, request.getWalletId());
            case KERI:
                return handleKeriVerification(request, eventId, request.getWalletId());
            default:
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_WALLET_TYPE")
                        .withDetail("The specified wallet type is not supported.")
                        .withStatus(BAD_REQUEST)
                        .build());
        }
    }

    private Either<Problem, EventSummary> validateEvent(String eventId) {
        Either<Problem, Optional<EventSummary>> eventDetailsResult = chainFollowerClient.findEventById(eventId);

        if (eventDetailsResult.isLeft()) {
            return Either.left(eventDetailsResult.getLeft());
        }

        return eventDetailsResult.get()
                .map(Either::<Problem, EventSummary>right)
                .orElseGet(() -> Either.left(Problem.builder()
                        .withTitle("EVENT_NOT_FOUND")
                        .withDetail("Event not found, eventId: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build()));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Either<Problem, IsVerifiedResponse> handleCardanoVerification(DiscordCheckVerificationRequest request, String eventId, String walletId) {

        String signatureM = request.getCoseSignature().orElse(null);

        if (signatureM == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String publicKeyM = request.getCosePublicKey().orElse(null);

        if (publicKeyM == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_PUBLIC_KEY")
                    .withDetail("Missing public key.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        // Verify signature specific to Cardano wallets
        Either<Problem, VerificationResult> verificationResult = verifySignature(signatureM, publicKeyM);

        if (verificationResult.isLeft()) {
            return Either.left(verificationResult.getLeft());
        }

        VerificationResult verificationData = verificationResult.get();
        String msg = verificationData.getMessage();
        var items = msg.split("\\|");

        if (items.length != 2) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, invalid signed message.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var discordIdHash = items[0];
        var cip30Secret = items[1];

        if (!request.getSecret().equals(cip30Secret)) {
            return Either.left(Problem.builder()
                    .withTitle("SECRET_MISMATCH")
                    .withDetail("Request Secret and CIP-30 secret mismatch.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        Optional<String> maybeAddress = verificationData.getAddress();

        if (!maybeAddress.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, must have asdress in CIP-30 signature.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        String address = maybeAddress.get();

        if (!walletId.equals(address)) {
            return Either.left(Problem.builder()
                    .withTitle("ADDRESS_MISMATCH")
                    .withDetail(String.format("Address mismatch, walletId: %s, address: %s", walletId, address))
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var stakeAddressCheckE = StakeAddress.checkStakeAddress(network, walletId);

        if (stakeAddressCheckE.isEmpty()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
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

        var maybePendingVerification = userVerificationRepository.findPendingVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybePendingVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("NO_PENDING_VERIFICATION")
                    .withDetail("No pending verification found for discordIdHash:" + discordIdHash)
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        var pendingVerification = maybePendingVerification.get();
        boolean isSecretCodeMatch = pendingVerification.getSecretCode().equals(cip30Secret)
                && pendingVerification.getSecretCode().equals(request.getSecret());

        if (!isSecretCodeMatch) {
            return Either.left(Problem.builder()
                    .withTitle("AUTH_FAILED")
                    .withDetail("Invalid secret and / or discordIdHash.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var pendingUserVerification = maybePendingVerification.orElseThrow();

        var now = LocalDateTime.now(clock);

        var isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
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

    private Either<Problem, VerificationResult> verifySignature(String signature, String publicKey) {
        CIP30Verifier verifier = new CIP30Verifier(signature, publicKey);
        var result = verifier.verify();

        if (!result.isValid()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        String msg = result.getMessage(MessageFormat.TEXT);
        Optional<String> maybeAddress = result.getAddress(AddressFormat.TEXT);

        return Either.right(new VerificationResult(msg, maybeAddress));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Either<Problem, IsVerifiedResponse> handleKeriVerification(DiscordCheckVerificationRequest request, String eventId, String walletId) {

        String signatureM = request.getKeriSignedMessage().orElse(null);

        if (signatureM == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String payload = request.getKeriPayload().orElse(null);

        if (payload == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing payload.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String oobi = request.getOobi().orElse(null);

        if (oobi == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing oobi.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var items = payload.split("\\|");
        var discordIdHash = items[0];
        var secret = items[1];

        var maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
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

        var maybePendingVerification = userVerificationRepository.findPendingVerificationBasedOnDiscordUserHash(eventId, discordIdHash);

        if (maybePendingVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("NO_PENDING_VERIFICATION")
                    .withDetail("No pending verification found for discordIdHash:" + discordIdHash)
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        var pendingVerification = maybePendingVerification.get();
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

        var pendingUserVerification = maybePendingVerification.orElseThrow();

        var now = LocalDateTime.now(clock);

        var isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
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

        if (oobiCheckResult.isRight()) {
            log.info("OOBI already registered: {}", oobiCheckResult);
            Either<Problem, Boolean> verificationResult = keriVerificationClient.verifySignature(walletId, signatureM, payload);

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

        log.info("OOBI not registered yet: {}", oobi);
        // Step 2: Register OOBI if not already registered
        Either<Problem, Boolean> oobiRegistrationResult = keriVerificationClient.registerOOBI(oobi);

        if (oobiRegistrationResult.isLeft()) {
            return Either.left(oobiRegistrationResult.getLeft());
        }

        log.info("OOBI registered successfully: {}", oobi);

        // Step 3: Attempt to verify OOBI registration up to 10 times
        Either<Problem, String> oobiFetchResult = keriVerificationClient.getOOBI(oobi, 60);
        if (oobiFetchResult.isLeft()) {
            return Either.left(oobiFetchResult.getLeft());
        }

        // Step 4: Verify signature after OOBI registration
        Either<Problem, Boolean> verificationResult = keriVerificationClient.verifySignature(walletId, signatureM, payload);

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

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerifiedBasedOnWalletId(IsVerifiedRequest isVerifiedRequest) {
        var isVerified = userVerificationRepository.findCompletedVerifications(isVerifiedRequest.getEventId(), isVerifiedRequest.getWalletId())
                .stream().findFirst()
                .map(uv -> new IsVerifiedResponse(true)).orElse(new IsVerifiedResponse(false));

        return Either.right(isVerified);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerifiedBasedOnDiscordIdHash(String eventId, String discordIdHash) {
        var isVerified = userVerificationRepository.findCompletedVerificationBasedOnDiscordUserHash(eventId, discordIdHash)
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
