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
import org.cardano.foundation.voting.service.pass.CodeGenService;
import org.cardano.foundation.voting.service.sms.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
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
public class DefaultSMSSMSUserVerificationService implements SMSUserVerificationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private SMSService smsService;


    @Autowired
    private UserVerificationRepository userVerificationRepository;

    @Autowired
    private SaltHolder saltHolder;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Autowired
    private Clock clock;

    @Autowired
    private CodeGenService codeGenService;

    @Value("${friendly.custom.name}")
    private String friendlyCustomName;

    @Value("${validation.expiration.time.minutes}")
    private int validationExpirationTimeMinutes;

    @Value("${max.pending.verification.attempts}")
    private int maxPendingVerificationAttempts;

    @Override
    @Transactional
    public Either<Problem, SMSStartVerificationResponse> startVerification(SMSStartVerificationRequest startVerificationRequest) {
        String eventId = startVerificationRequest.getEventId();
        String stakeAddress = startVerificationRequest.getStakeAddress();

        var stakeAddressCheckE = stakeAddressVerificationService.checkStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isEmpty()) {
            return Either.left(stakeAddressCheckE.getLeft());
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

        var maybeUserVerificationStakeAddress = userVerificationRepository.findAllCompletedPerStake(
                eventId,
                stakeAddress
        ).stream().findFirst();

        if (maybeUserVerificationStakeAddress.isPresent()) {
            log.info("User verification already completed (stake):{}", maybeUserVerificationStakeAddress.orElseThrow());

            var userVerification = maybeUserVerificationStakeAddress.get();
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
            log.error("Invalid phone number, phone hash:{}", saltedPhoneHash(startVerificationRequest.getPhoneNumber()));

            return Either.left(Problem.builder()
                    .withTitle("INVALID_PHONE_NUMBER")
                    .withDetail("Invalid phone number format, correct format is: e.g. +48 881 35 00 67 (with or without spaces)")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var phoneNum = maybePhoneNum.orElseThrow();
        var formattedPhoneStr = PhoneNumberUtil.getInstance().format(phoneNum, INTERNATIONAL);
        var phoneHash = saltedPhoneHash(formattedPhoneStr);

        var maybeUserVerificationPhoneHash = userVerificationRepository.findAllCompletedPerPhone(
                eventId,
                phoneHash
        ).stream().findFirst();

        if (maybeUserVerificationPhoneHash.isPresent()) {
            log.info("User verification already completed (phone used):{}", maybeUserVerificationPhoneHash.orElseThrow());

            var userVerification = maybeUserVerificationPhoneHash.get();
            if (userVerification.getStatus() == VERIFIED) {
                log.info("Phone already used, phoneHash:{}", userVerification.getPhoneNumberHash());

                return Either.left(Problem.builder()
                        .withTitle("PHONE_ALREADY_USED")
                        .withDetail("Phone already used.")
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }

        int pendingPerStakeAddressCount = userVerificationRepository.findPendingPerStakeAddressPerPhoneCount(eventId, stakeAddress, phoneHash);
        if (pendingPerStakeAddressCount >= maxPendingVerificationAttempts) {
            return Either.left(Problem.builder()
                    .withTitle("MAX_VERIFICATION_ATTEMPTS_REACHED")
                    .withDetail(String.format("Max verification attempts reached for eventId:%s, stakeAddress:%s. Try again in 24 hours.", eventId, stakeAddress))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var randomVerificationCode = codeGenService.generateRandomCode();
        var textMsg = String.format("%s. %s", randomVerificationCode, friendlyCustomName).trim();

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

        var startVerificationResponse = new SMSStartVerificationResponse(
                saved.getEventId(),
                saved.getStakeAddress(),
                saved.getRequestId(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );

        return Either.right(startVerificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> checkVerification(SMSCheckVerificationRequest checkVerificationRequest) {
        String eventId = checkVerificationRequest.getEventId();
        String stakeAddress = checkVerificationRequest.getStakeAddress();

        var stakeAddressCheckE = stakeAddressVerificationService.checkStakeAddress(stakeAddress);
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
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

        var maybeUserVerification = userVerificationRepository.findAllCompletedPerStake(
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
        pendingUserVerification.setUpdatedAt(now);

        var saved = userVerificationRepository.saveAndFlush(pendingUserVerification);

        return Either.right(new IsVerifiedResponse(saved.getStatus() == VERIFIED));
    }

    @Override
    @Transactional(readOnly = true)
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

        var maybeUserVerification = userVerificationRepository.findAllCompletedPerStake(
                isVerifiedRequest.getEventId(),
                isVerifiedRequest.getStakeAddress()
        ).stream().findFirst();

        if (maybeUserVerification.isEmpty()) {
            log.info("Completed user verification not found for:{}", isVerifiedRequest.getStakeAddress());

            return Either.right(new IsVerifiedResponse(false));
        }

        var userVerification = maybeUserVerification.orElseThrow();
        log.info("Using verification:{}", userVerification);

        var status = userVerification.getStatus();

        return Either.right(new IsVerifiedResponse(status == VERIFIED));
    }

    @Override
    @Transactional
    public void removeUserVerification(UserVerification userVerification) {
        userVerificationRepository.delete(userVerification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVerification> findAllForEvent(String eventId) {
        return userVerificationRepository.findAllByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVerification> findAllPending(String eventId) {
        return userVerificationRepository.findAllPending(eventId);
    }

    private static Optional<Phonenumber.PhoneNumber> isValidNumber(String userEnteredPhoneNumber) {
        try {
            var phoneNumber = PhoneNumberUtil.getInstance().parse(userEnteredPhoneNumber, null);

            if (PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
                return Optional.of(phoneNumber);
            }

            return Optional.empty();
        } catch (NumberParseException e) {
            log.warn("Invalid phone number.");
            return Optional.empty();
        }
    }

    private String saltedPhoneHash(String phoneNumber) {
        var value = saltHolder.salt() + phoneNumber;

        byte[] bytes = value.getBytes(UTF_8);

        return HexFormat.of().formatHex(blake2bHash256(bytes));
    }

}
