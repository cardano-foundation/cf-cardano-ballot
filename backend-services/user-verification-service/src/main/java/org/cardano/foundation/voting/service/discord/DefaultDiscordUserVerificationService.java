package org.cardano.foundation.voting.service.discord;

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
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.MessageFormat;
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
    public Either<Problem, IsVerifiedResponse> checkVerification(String eventId, DiscordCheckVerificationRequest checkVerificationRequest) {
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

        if (event.finished()) {
            log.warn("Event already finished:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var stakeAddress = checkVerificationRequest.getStakeAddress();

        var coseSignature = checkVerificationRequest.getCoseSignature();
        var cosePublicKey = checkVerificationRequest.getCosePublicKey();

        var cip30Verifier = new CIP30Verifier(coseSignature, cosePublicKey);
        var cip30VerificationResult = cip30Verifier.verify();

        if (!cip30VerificationResult.isValid()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var msg = cip30VerificationResult.getMessage(MessageFormat.TEXT);
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
        var secret = items[1];

        var maybeAddress = cip30VerificationResult.getAddress(AddressFormat.TEXT);

        if (maybeAddress.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, must have asdress in CIP-30 signature.")
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var address = maybeAddress.orElseThrow();

        if (!stakeAddress.equals(address)) {
            return Either.left(Problem.builder()
                    .withTitle("ADDRESS_MISMATCH")
                    .withDetail(String.format("Address mismatch, stakeAddress: %s, address: %s", stakeAddress, address))
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var stakeAddressCheckE = StakeAddress.checkStakeAddress(network, stakeAddress);

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

        boolean isSecretCodeMatch = maybePendingVerification.get().getSecretCode().equals(secret);

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
                    .withDetail(String.format("Secret code: %s expired for stakeAddress: %s and discordHashId:%s", secret, stakeAddress, discordIdHash))
                    .withStatus(BAD_REQUEST)
                    .with("discordIdHash", discordIdHash)
                    .with("stakeAddress", stakeAddress)
                    .build());
        }

        pendingUserVerification.setStakeAddress(Optional.of(stakeAddress));
        pendingUserVerification.setUpdatedAt(now);
        pendingUserVerification.setStatus(VERIFIED);

        return Either.right(new IsVerifiedResponse(true));
    }

    @Override
    public Either<Problem, IsVerifiedResponse> isVerifiedBasedOnStakeAddress(IsVerifiedRequest isVerifiedRequest) {
        var isVerified = userVerificationRepository.findCompletedVerification(isVerifiedRequest.getEventId(), isVerifiedRequest.getStakeAddress())
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
