package org.cardano.foundation.voting.service.verify;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.cardano.foundation.voting.repository.UserVerificationRepository;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.sms.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash256;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Channel.SMS;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Provider.AWS_SNS;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Status.PENDING;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Status.VERIFIED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class UserVerificationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private SMSService smsService;


    @Autowired
    private UserVerificationRepository userVerificationRepository;


    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Autowired
    private Clock clock;

    @Value("${friendly.custom.name}")
    private String friendlyCustomName;

    @Value("${validation.expiration.time.minutes}")
    private int validationExpirationTimeMinutes;

    @Value("${max.verification.attempts}")
    private int maxVerificationAttempts;

    private final static SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public Either<Problem, StartVerificationResponse> startVerification(StartVerificationRequest startVerificationRequest) {
        String eventId = startVerificationRequest.getEventId();
        String stakeAddress = startVerificationRequest.getStakeAddress();

        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(stakeAddress);
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var activeEventE = chainFollowerClient.findEventById(eventId);

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        var maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.warn("Active event not found:{}", eventId);

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

        var maybeUserVerification = userVerificationRepository.findAllCompleted(
                eventId,
                stakeAddress
        ).stream().findFirst();

        if (maybeUserVerification.isPresent()) {
            log.info("User verification already completed:{}", maybeUserVerification.orElseThrow());

            var userVerification = maybeUserVerification.get();
            if (userVerification.getStatus() == VERIFIED) {
                return Either.left(Problem.builder()
                        .withTitle("USER_ALREADY_VERIFIED")
                        .withDetail("User already verified, stakeAddress:" + stakeAddress)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }

        var maybePhoneNum = isValidNumber(startVerificationRequest.getPhoneNumber());

        if (maybePhoneNum.isEmpty()) {
            log.error("Invalid phone number:{}", startVerificationRequest.getPhoneNumber());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_PHONE_NUMBER")
                    .withDetail("Invalid phone number, phone number:" + startVerificationRequest.getPhoneNumber())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var phoneNum = maybePhoneNum.orElseThrow();
        var formattedPhoneStr = PhoneNumberUtil.getInstance().format(phoneNum, INTERNATIONAL);
        var phoneHash = HexFormat.of().formatHex(blake2bHash256(formattedPhoneStr.getBytes(UTF_8)));

        int pendingPerStakeAddressCount = userVerificationRepository.findPendingPerStakeAddressPerPhoneCount(eventId, stakeAddress, phoneHash);
        if (pendingPerStakeAddressCount > maxVerificationAttempts) {
            return Either.left(Problem.builder()
                    .withTitle("MAX_VERIFICATION_ATTEMPTS_REACHED")
                    .withDetail(String.format("Max verification attempts reached for eventId:%s, stakeAddress:%s. Try again in 24 hours.", eventId, stakeAddress))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var randomVerificationCode = SECURE_RANDOM.nextInt(100000, 999999);

        // TODO localise?
        var textMsg = String.format("Verification Code: %s. %s", randomVerificationCode, friendlyCustomName).trim();

        var smsVerificationResponseE = smsService.publishTextMessage(textMsg, phoneNum);

        if (smsVerificationResponseE.isLeft()) {
            return Either.left(smsVerificationResponseE.getLeft());
        }

        var smsVerificationResponse = smsVerificationResponseE.get();

        log.info("SMS sent to:{} (blake2b 256 hash), SNS msgId:{}, code:{}", phoneHash, smsVerificationResponse.requestId(), randomVerificationCode);

        var now = LocalDateTime.now(clock);

        var newUserVerification = UserVerification.builder()
                .id(UUID.randomUUID().toString())
                .eventId(eventId)
                .channel(SMS)
                .provider(AWS_SNS)
                .status(PENDING)
                .phoneNumberHash(phoneHash)
                .stakeAddress(stakeAddress)
                .verificationCode(String.valueOf(randomVerificationCode))
                .requestId(smsVerificationResponse.requestId())
                .expiresAt(now.plusMinutes(validationExpirationTimeMinutes))
                .build();

        var saved = userVerificationRepository.saveAndFlush(newUserVerification);

        var startVerificationResponse = new StartVerificationResponse(
                saved.getEventId(),
                saved.getStakeAddress(),
                saved.getRequestId(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );

        return Either.right(startVerificationResponse);
    }

    @Transactional
    public Either<Problem, IsVerifiedResponse> checkVerification(CheckVerificationRequest checkVerificationRequest) {
        String eventId = checkVerificationRequest.getEventId();
        String stakeAddress = checkVerificationRequest.getStakeAddress();

        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(stakeAddress);
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var activeEventE = chainFollowerClient.findEventById(eventId);

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        var maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Active event not found:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybeUserVerification = userVerificationRepository.findAllCompleted(
                eventId,
                stakeAddress
        ).stream().findFirst();

        if (maybeUserVerification.isPresent()) {
            log.info("User verification already completed:{}", maybeUserVerification.orElseThrow());

            var userVerification = maybeUserVerification.get();
            if (userVerification.getStatus() == VERIFIED) {
                return Either.left(Problem.builder()
                        .withTitle("USER_ALREADY_VERIFIED")
                        .withDetail("User already verified, stakeAddress:" + stakeAddress)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }

        var maybePendingRequest = userVerificationRepository.findPendingVerificationsByEventIdAndStakeAddressAndRequestId(
                eventId,
                stakeAddress,
                checkVerificationRequest.getRequestId()
        );

        if (maybePendingRequest.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("PENDING_USER_VERIFICATION_NOT_FOUND")
                    .withDetail("User verification not found, stakeAddress:" + stakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var pendingUserVerification = maybePendingRequest.orElseThrow();

        if (pendingUserVerification.getStatus() == VERIFIED) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_ALREADY_VERIFIED")
                    .withDetail("User verification already verified, stakeAddress:" + stakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var now = LocalDateTime.now(clock);

        var isValidVerificationCode = pendingUserVerification.getVerificationCode().trim().equals(checkVerificationRequest.getVerificationCode().trim());
        if (!isValidVerificationCode) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VERIFICATION_CODE")
                    .withDetail("Invalid verification code, verificationCode:" + checkVerificationRequest.getVerificationCode())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
        if (isCodeExpired) {
                return Either.left(Problem.builder()
                        .withTitle("VERIFICATION_EXPIRED")
                        .withDetail(String.format("Verification code: %s expired for stakeAddress: %s", checkVerificationRequest.getVerificationCode(), stakeAddress))
                        .withStatus(BAD_REQUEST)
                        .build());
        }

        pendingUserVerification.setStatus(VERIFIED);
        pendingUserVerification.setPhoneNumberHash(Optional.empty()); // no need to keep this anymore
        pendingUserVerification.setUpdatedAt(now);

        var saved = userVerificationRepository.saveAndFlush(pendingUserVerification);

        return Either.right(new IsVerifiedResponse(saved.getStatus() == VERIFIED));
    }

    @Transactional
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        var activeEventE = chainFollowerClient.findEventById(isVerifiedRequest.getEventId());

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        var maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Active event not found:{}", isVerifiedRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + isVerifiedRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", isVerifiedRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + isVerifiedRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybeUserVerification = userVerificationRepository.findAllCompleted(
                isVerifiedRequest.getEventId(),
                isVerifiedRequest.getStakeAddress()
        ).stream().findFirst();

        if (maybeUserVerification.isEmpty()) {
            log.info("Completed or pending user verification not found for:{}", isVerifiedRequest.getStakeAddress());

            return Either.right(new IsVerifiedResponse(false));
        }

        var userVerification = maybeUserVerification.orElseThrow();
        log.info("Using verification:{}", userVerification);

        var status = userVerification.getStatus();

        return Either.right(new IsVerifiedResponse(status == VERIFIED));
    }

    private static Optional<Phonenumber.PhoneNumber> isValidNumber(String userEnteredPhoneNumber) {
        try {
            var phoneNumber = PhoneNumberUtil.getInstance().parse(userEnteredPhoneNumber, null);

            if (PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
                return Optional.of(phoneNumber);
            }

            return Optional.empty();
        } catch (NumberParseException e) {
            log.warn("Invalid phone number:{}", userEnteredPhoneNumber);
            return Optional.empty();
        }
    }

}
