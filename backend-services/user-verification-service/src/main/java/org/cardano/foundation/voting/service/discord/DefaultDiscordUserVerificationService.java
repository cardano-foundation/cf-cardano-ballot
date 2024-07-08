package org.cardano.foundation.voting.service.discord;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
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

        Either<Problem, EventSummary> eventValidationResult = validateEvent(eventId);
        if (eventValidationResult.isLeft()) {
            return Either.left(eventValidationResult.getLeft());
        }

        String walletId = request.getWalletId();
        Optional<WalletType> walletIdType = request.getWalletIdType();

        if (walletIdType.isPresent()) {
            switch (walletIdType.get()) {
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
        } else {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_WALLET_TYPE")
                    .withDetail("Wallet type must be specified.")
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

    private Either<Problem, IsVerifiedResponse> handleCardanoVerification(DiscordCheckVerificationRequest request, String eventId, String walletId) {

        String signature = request.getCoseSignature().orElse(null);

        if (signature == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        String publicKey = request.getCosePublicKey().orElse(null);

        if (publicKey == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_PUBLIC_KEY")
                    .withDetail("Missing public key.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        // Verify signature specific to Cardano wallets
        Either<Problem, Tuple2<String, Optional<String>>> verificationResult = verifySignature(
                signature, publicKey);

        if (verificationResult.isLeft()) {
            return Either.left(verificationResult.getLeft());
        }

        Tuple2<String, Optional<String>> verificationData = verificationResult.get();
        String msg = verificationData._1;
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

        Optional<String> maybeAddress = verificationData._2;

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
        pendingVerification.setWalletIdType(request.getWalletIdType());
        pendingVerification.setUpdatedAt(LocalDateTime.now(clock));
        pendingVerification.setStatus(VERIFIED);
        userVerificationRepository.save(pendingVerification);

        return Either.right(new IsVerifiedResponse(true));
    }

    private Either<Problem, Tuple2<String, Optional<String>>> verifySignature(String signature, String publicKey) {
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

        return Either.right(new Tuple2<>(msg, maybeAddress));
    }

    private Either<Problem, IsVerifiedResponse> handleKeriVerification(DiscordCheckVerificationRequest request, String eventId, String walletId) {
        System.out.println("\nhandleKeriVerification");
        String signature = request.getKeriSignedMessage().orElse(null);
        System.out.println("keriSignedMessage");
        System.out.println(signature);
        System.out.println("request");
        System.out.println(request);
        System.out.println("walletId");
        System.out.println(walletId);

        if (signature == null) {
            return Either.left(Problem.builder()
                    .withTitle("MISSING_SIGNATURE")
                    .withDetail("Missing signature.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }
        // Example: Simulate checking a condition specific to Keri
        boolean conditionMet = checkKeriCondition(request);

        if (!conditionMet) {
            return Either.left(Problem.builder()
                    .withTitle("KERI_VERIFICATION_FAILED")
                    .withDetail("The Keri-specific condition was not met.")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return Either.right(new IsVerifiedResponse(true));
    }

    private boolean checkKeriCondition(DiscordCheckVerificationRequest request) {
        return true;
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
