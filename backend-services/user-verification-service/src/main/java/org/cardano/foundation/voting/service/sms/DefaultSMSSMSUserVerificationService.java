package org.cardano.foundation.voting.service.sms;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.*;
import org.cardano.foundation.voting.domain.entity.SMSUserVerification;
import org.cardano.foundation.voting.domain.sms.SMSCheckVerificationRequest;
import org.cardano.foundation.voting.domain.sms.SMSStartVerificationRequest;
import org.cardano.foundation.voting.domain.sms.SMSStartVerificationResponse;
import org.cardano.foundation.voting.repository.SMSUserVerificationRepository;
import org.cardano.foundation.voting.service.pass.CodeGenService;
import org.cardano.foundation.voting.utils.Addresses;
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
import static org.cardano.foundation.voting.domain.VerificationStatus.PENDING;
import static org.cardano.foundation.voting.domain.VerificationStatus.VERIFIED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultSMSSMSUserVerificationService implements SMSUserVerificationService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private SMSService smsService;

    @Autowired
    private SMSUserVerificationRepository smsUserVerificationRepository;

    @Autowired
    private SaltHolder saltHolder;

    @Autowired
    private Clock clock;

    @Autowired
    private ChainNetwork network;

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
        val eventId = startVerificationRequest.getEventId();

        val walletId = startVerificationRequest.getWalletId();
        val walletType = startVerificationRequest.getWalletType();

        val walletIdCheckE = Addresses.checkWalletId(network, walletType, walletId);
        if (walletIdCheckE.isEmpty()) {
            return Either.left(walletIdCheckE.getLeft());
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

        if (!event.userBased() && walletType == WalletType.KERI) {
            log.warn("Keri wallet not supported for BALANCE or STAKE type event");

            return Either.left(Problem.builder()
                    .withTitle("WALLET_NOT_SUPPORTED")
                    .withDetail("Keri wallet not supported for BALANCE or STAKE type event")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        if (event.finished()) {
            log.warn("Event already finished:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val maybeUserVerificationWalletId = smsUserVerificationRepository.findAllCompletedPerWalletId(
                eventId,
                walletType,
                walletId
        ).stream().findFirst();

        if (maybeUserVerificationWalletId.isPresent()) {
            log.info("User verification already completed (walletId):{}", maybeUserVerificationWalletId.orElseThrow());

            val userVerification = maybeUserVerificationWalletId.get();
            if (userVerification.getStatus() == VERIFIED) {
                return Either.left(Problem.builder()
                        .withTitle("USER_ALREADY_VERIFIED")
                        .withDetail("User already verified, walletId:" + walletId)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }

        val maybePhoneNum = isValidNumber(startVerificationRequest.getPhoneNumber());
        if (maybePhoneNum.isEmpty()) {
            log.error("Invalid phone number, phone hash:{}", saltedPhoneHash(startVerificationRequest.getPhoneNumber()));

            return Either.left(Problem.builder()
                    .withTitle("INVALID_PHONE_NUMBER")
                    .withDetail("Invalid phone number format, correct format is: e.g. +48 881 35 00 67 (with or without spaces)")
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val phoneNum = maybePhoneNum.orElseThrow();
        val formattedPhoneStr = PhoneNumberUtil.getInstance().format(phoneNum, INTERNATIONAL);
        val phoneHash = saltedPhoneHash(formattedPhoneStr);

        val maybeUserVerificationPhoneHash = smsUserVerificationRepository.findAllCompletedPerPhone(
                eventId,
                phoneHash
        ).stream().findFirst();

        if (maybeUserVerificationPhoneHash.isPresent()) {
            log.info("User verification already completed (phone used):{}", maybeUserVerificationPhoneHash.orElseThrow());

            val userVerification = maybeUserVerificationPhoneHash.get();
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

        int pendingPerWalletIdCount = smsUserVerificationRepository.findPendingPerWalletIdPerPhoneCount(eventId, walletId, phoneHash);
        if (pendingPerWalletIdCount >= maxPendingVerificationAttempts) {
            return Either.left(Problem.builder()
                    .withTitle("MAX_VERIFICATION_ATTEMPTS_REACHED")
                    .withDetail(String.format("Max verification attempts reached for eventId:%s, walletId:%s. Try again in 24 hours.", eventId, walletId))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val randomVerificationCode = codeGenService.generateRandomCode();
        // TODO transaction service based on phone number prefix?
        val textMsg = String.format("Auth Code: %s. %s", randomVerificationCode, friendlyCustomName).trim();

        val smsVerificationResponseE = smsService.publishTextMessage(textMsg, phoneNum);

        if (smsVerificationResponseE.isLeft()) {
            return Either.left(smsVerificationResponseE.getLeft());
        }

        val smsVerificationResponse = smsVerificationResponseE.get();

        log.info("SMS sent to:{} (blake2b 256 hash), SNS msgId:{}, code:{}", phoneHash, smsVerificationResponse.requestId(), randomVerificationCode);

        val now = LocalDateTime.now(clock);

        val newUserVerification = SMSUserVerification.builder()
                .id(UUID.randomUUID().toString())
                .eventId(eventId)
                .status(PENDING)
                .phoneNumberHash(phoneHash)
                .walletId(walletId)
                .walletType(walletType)
                .verificationCode(String.valueOf(randomVerificationCode))
                .requestId(smsVerificationResponse.requestId())
                .expiresAt(now.plusMinutes(validationExpirationTimeMinutes))
                .build();

        val saved = smsUserVerificationRepository.saveAndFlush(newUserVerification);

        val startVerificationResponse = new SMSStartVerificationResponse(
                saved.getEventId(),
                saved.getWalletId(),
                saved.getWalletType(),
                saved.getRequestId(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );

        return Either.right(startVerificationResponse);
    }

    @Override
    @Transactional
    public Either<Problem, IsVerifiedResponse> checkVerification(SMSCheckVerificationRequest checkVerificationRequest) {
        String eventId = checkVerificationRequest.getEventId();

        val walletId = checkVerificationRequest.getWalletId();
        val walletType = checkVerificationRequest.getWalletType();

        val walletIdCheckE = Addresses.checkWalletId(network, walletType, walletId);
        if (walletIdCheckE.isEmpty()) {
            return Either.left(walletIdCheckE.getLeft());
        }

        val activeEventE = chainFollowerClient.findEventById(eventId);

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        val maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Event not found:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", eventId);

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val maybeUserVerification = smsUserVerificationRepository.findAllCompletedPerWalletId(
                eventId,
                walletType,
                walletId
        ).stream().findFirst();

        if (maybeUserVerification.isPresent()) {
            log.info("User verification already completed:{}", maybeUserVerification.orElseThrow());

            val userVerification = maybeUserVerification.get();
            if (userVerification.getStatus() == VERIFIED) {
                return Either.left(Problem.builder()
                        .withTitle("USER_ALREADY_VERIFIED")
                        .withDetail("User already verified, walletId:" + walletId)
                        .withStatus(BAD_REQUEST)
                        .build()
                );
            }
        }

        val maybePendingRequest = smsUserVerificationRepository.findPendingVerificationsByEventIdAndWalletIdAndRequestId(
                eventId,
                walletId,
                checkVerificationRequest.getRequestId()
        );

        if (maybePendingRequest.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("PENDING_USER_VERIFICATION_NOT_FOUND")
                    .withDetail("User verification not found, walletId:" + walletId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val pendingUserVerification = maybePendingRequest.orElseThrow();

        if (pendingUserVerification.getStatus() == VERIFIED) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_ALREADY_VERIFIED")
                    .withDetail("User verification already verified, walletId:" + walletId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        val now = LocalDateTime.now(clock);

        val isValidVerificationCode = pendingUserVerification.getVerificationCode().trim().equals(checkVerificationRequest.getVerificationCode().trim());
        if (!isValidVerificationCode) {
            return Either.left(Problem.builder()
                    .withTitle("INVALID_VERIFICATION_CODE")
                    .withDetail("Invalid verification code, verificationCode:" + checkVerificationRequest.getVerificationCode())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val isCodeExpired = now.isAfter(pendingUserVerification.getExpiresAt());
        if (isCodeExpired) {
                return Either.left(Problem.builder()
                        .withTitle("VERIFICATION_EXPIRED")
                        .withDetail(String.format("Verification code: %s expired for walletId: %s", checkVerificationRequest.getVerificationCode(), walletId))
                        .withStatus(BAD_REQUEST)
                        .build());
        }

        pendingUserVerification.setStatus(VERIFIED);
        pendingUserVerification.setUpdatedAt(now);

        val saved = smsUserVerificationRepository.saveAndFlush(pendingUserVerification);

        return Either.right(new IsVerifiedResponse(saved.getStatus() == VERIFIED));
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        val activeEventE = chainFollowerClient.findEventById(isVerifiedRequest.getEventId());

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        val maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Event not found:{}", isVerifiedRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + isVerifiedRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", isVerifiedRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + isVerifiedRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        val maybeUserVerification = smsUserVerificationRepository.findAllCompletedPerWalletId(
                isVerifiedRequest.getEventId(),
                isVerifiedRequest.getWalletType(),
                isVerifiedRequest.getWalletId()
        ).stream().findFirst();

        if (maybeUserVerification.isEmpty()) {
            log.info("Completed user verification not found for:{}", isVerifiedRequest.getWalletId());

            return Either.right(new IsVerifiedResponse(false));
        }

        val userVerification = maybeUserVerification.orElseThrow();
        log.info("Using verification:{}", userVerification);

        val status = userVerification.getStatus();

        return Either.right(new IsVerifiedResponse(status == VERIFIED));
    }

    @Override
    @Transactional
    public void removeUserVerification(SMSUserVerification SMSUserVerification) {
        smsUserVerificationRepository.delete(SMSUserVerification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SMSUserVerification> findAllForEvent(String eventId) {
        return smsUserVerificationRepository.findAllByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SMSUserVerification> findAllPending(String eventId) {
        return smsUserVerificationRepository.findAllPending(eventId);
    }

    private static Optional<Phonenumber.PhoneNumber> isValidNumber(String userEnteredPhoneNumber) {
        try {
            val phoneNumber = PhoneNumberUtil.getInstance().parse(userEnteredPhoneNumber, null);

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
        val value = saltHolder.salt() + phoneNumber;

        byte[] bytes = value.getBytes(UTF_8);

        return HexFormat.of().formatHex(blake2bHash256(bytes));
    }

}
