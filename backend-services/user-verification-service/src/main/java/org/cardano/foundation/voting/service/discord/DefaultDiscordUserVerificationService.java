package org.cardano.foundation.voting.service.discord;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DefaultDiscordUserVerificationService implements DiscordUserVerificationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private DiscordUserVerificationRepository userVerificationRepository;

    @Autowired
    private Clock clock;

    @Value("${discord.bot.eventId.binding}")
    private String discordBotEventIdBinding;

    @Value("${validation.expiration.time.minutes}")
    private int validationExpirationTimeMinutes;

    @Autowired
    private CardanoNetwork network;

    @Override
    @Transactional
    public Either<Problem, DiscordStartVerificationResponse> startVerification(DiscordStartVerificationRequest startVerificationRequest) {
        var discordIdHash = startVerificationRequest.getDiscordIdHash();

        var maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(discordBotEventIdBinding, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .build());
        }

        var createdAt = LocalDateTime.now(clock);
        var expiresAt = createdAt.plusMinutes(validationExpirationTimeMinutes);

        var discordUserVerification = DiscordUserVerification.builder()
                .discordIdHash(discordIdHash)
                .secretCode(startVerificationRequest.getSecret())
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .status(PENDING)
                .build();

        var eventDetails = chainFollowerClient.findEventById(discordBotEventIdBinding);

        if (eventDetails.isEmpty()) {
            log.error("event error:{}", eventDetails.getLeft());

            return Either.left(eventDetails.getLeft());
        }

        var maybeEvent = eventDetails.get();
        if (maybeEvent.isEmpty()) {
            log.warn("Active event not found:{}", discordBotEventIdBinding);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + discordBotEventIdBinding)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.warn("Event already finished:{}", discordBotEventIdBinding);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + discordBotEventIdBinding)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var saved = userVerificationRepository.saveAndFlush(discordUserVerification);

        return Either.right(DiscordStartVerificationResponse.builder()
                .eventId(discordBotEventIdBinding)
                .discordIdHash(saved.getDiscordIdHash())
                .status(saved.getStatus())
                .build()
        );
    }

    @Override
    @Transactional
    public Either<Problem, IsVerifiedResponse> checkVerification(DiscordCheckVerificationRequest checkVerificationRequest) {
        var eventDetails = chainFollowerClient.findEventById(discordBotEventIdBinding);

        if (eventDetails.isEmpty()) {
            log.error("event error:{}", eventDetails.getLeft());

            return Either.left(eventDetails.getLeft());
        }

        var maybeEvent = eventDetails.get();
        if (maybeEvent.isEmpty()) {
            log.warn("Active event not found:{}", discordBotEventIdBinding);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + discordBotEventIdBinding)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.warn("Event already finished:{}", discordBotEventIdBinding);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + discordBotEventIdBinding)
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
                    .build()
            );
        }

        var msg = cip30VerificationResult.getMessage(MessageFormat.TEXT);
        var items = msg.split("\\|");

        if (items.length != 2) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_CIP-30-SIGNATURE")
                    .withDetail("Invalid CIP-30 signature, invalid signed message.")
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
                    .build()
            );
        }

        var address = maybeAddress.orElseThrow();

        if (!stakeAddress.equals(address)) {
            return Either.left(Problem.builder()
                    .withTitle("ADDRESS_MISMATCH")
                    .withDetail(String.format("Address mismatch, stakeAddress: %s, address: %s", stakeAddress, address))
                    .build()
            );
        }

        var stakeAddressCheckE = StakeAddress.checkStakeAddress(network, stakeAddress);

        if (stakeAddressCheckE.isEmpty()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var maybeCompletedVerificationBasedOnDiscordUserHash = userVerificationRepository
                .findCompletedVerificationBasedOnDiscordUserHash(discordBotEventIdBinding, discordIdHash);

        if (maybeCompletedVerificationBasedOnDiscordUserHash.isPresent()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_ALREADY_VERIFIED")
                    .withDetail("User already verified.")
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        var maybePendingVerification = userVerificationRepository.findPendingVerificationBasedOnDiscordUserHash(discordBotEventIdBinding, discordIdHash);

        if (maybePendingVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("NO_PENDING_VERIFICATION")
                    .withDetail("No pending verification found for discordIdHash:" + discordIdHash)
                    .with("discordIdHash", discordIdHash)
                    .build()
            );
        }

        boolean isSecretCodeMatch = maybePendingVerification.get().getSecretCode().equals(secret);

        if (!isSecretCodeMatch) {
            return Either.left(Problem.builder()
                    .withTitle("AUTH_FAILED")
                    .withDetail("Invalid secret and / or discordIdHash.")
                    .build()
            );
        }

        DiscordUserVerification pendingUserVerification = maybePendingVerification.orElseThrow();

        var now = LocalDateTime.now(clock);

        var isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
        if (isCodeExpired) {
            return Either.left(Problem.builder()
                    .withTitle("VERIFICATION_EXPIRED")
                    .withDetail(String.format("Secret code: %s expired for stakeAddress: %s and discordHashId:%s", secret, stakeAddress, discordIdHash))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        pendingUserVerification.setStakeAddress(Optional.of(stakeAddress));
        pendingUserVerification.setUpdatedAt(now);
        pendingUserVerification.setStatus(VERIFIED);

        return Either.right(new IsVerifiedResponse(true));
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        var eventId = isVerifiedRequest.getEventId();
        var stakeAddress = isVerifiedRequest.getStakeAddress();

        var isVerified = userVerificationRepository.findCompletedVerification(eventId, stakeAddress)
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
