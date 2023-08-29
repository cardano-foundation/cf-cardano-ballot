package org.cardano.foundation.voting.service.verify;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CheckVerificationRequest;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.StartVerificationRequest;
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

    private final static SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public Either<Problem, UserVerification> startVerification(StartVerificationRequest startVerificationRequest) {
        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(startVerificationRequest.getStakeAddress());
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(startVerificationRequest.getStakeAddress());
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var activeEventE = chainFollowerClient.findEventById(startVerificationRequest.getEventId());

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        var maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Active event not found:{}", startVerificationRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + startVerificationRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", startVerificationRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + startVerificationRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
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

        var randomCode = SECURE_RANDOM.nextInt(100000, 999999);

        var textMsg = String.format("Verification Code: %s. %s", randomCode, friendlyCustomName).trim();

        var smsVerificationResponseE = smsService.publishTextMessage(textMsg, phoneNum);

        if (smsVerificationResponseE.isLeft()) {
            return Either.left(smsVerificationResponseE.getLeft());
        }

        var smsVerificationResponse = smsVerificationResponseE.get();

        var formattedPhoneStr = PhoneNumberUtil.getInstance().format(phoneNum, INTERNATIONAL);
        var phoneHash = HexFormat.of().formatHex(blake2bHash256(formattedPhoneStr.getBytes(UTF_8)));

        log.info("SMS sent to:{} (blake2b 256 hash), SNS msgId:{}, code:{}", phoneHash, smsVerificationResponse.requestId(), randomCode);

        var userVerification = UserVerification.builder()
                .eventId(startVerificationRequest.getEventId())
                .channel(SMS)
                .provider(AWS_SNS)
                .status(PENDING)
                .phoneNumberHash(phoneHash)
                .stakeAddress(startVerificationRequest.getStakeAddress())
                .verificationCode(String.valueOf(randomCode))
                .requestId(smsVerificationResponse.requestId())
                .build();

        return Either.right(userVerificationRepository.saveAndFlush(userVerification));
    }

    @Transactional
    public Either<Problem, UserVerification> checkVerification(CheckVerificationRequest checkVerificationRequest) {
        var stakeAddressCheckE = stakeAddressVerificationService.checkIfAddressIsStakeAddress(checkVerificationRequest.getStakeAddress());
        if (stakeAddressCheckE.isLeft()) {
            return Either.left(stakeAddressCheckE.getLeft());
        }

        var stakeAddressNetworkCheck = stakeAddressVerificationService.checkStakeAddressNetwork(checkVerificationRequest.getStakeAddress());
        if (stakeAddressNetworkCheck.isLeft()) {
            return Either.left(stakeAddressNetworkCheck.getLeft());
        }

        var activeEventE = chainFollowerClient.findEventById(checkVerificationRequest.getEventId());

        if (activeEventE.isEmpty()) {
            log.error("Active event error:{}", activeEventE.getLeft());

            return Either.left(activeEventE.getLeft());
        }

        var maybeEvent = activeEventE.get();
        if (maybeEvent.isEmpty()) {
            log.error("Active event not found:{}", checkVerificationRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_NOT_FOUND")
                    .withDetail("Event not found, eventId:" + checkVerificationRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var event = maybeEvent.orElseThrow();

        if (event.finished()) {
            log.error("Event already finished:{}", checkVerificationRequest.getEventId());

            return Either.left(Problem.builder()
                    .withTitle("EVENT_ALREADY_FINISHED")
                    .withDetail("Event already finished, eventId:" + checkVerificationRequest.getEventId())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var maybePendingRequest = userVerificationRepository.findPendingVerificationsByEventIdAndStakeAddressAndRequestId(
                checkVerificationRequest.getEventId(),
                checkVerificationRequest.getStakeAddress(),
                checkVerificationRequest.getRequestId()
        );

        if (maybePendingRequest.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_NOT_FOUND")
                    .withDetail("User verification not found, stakeAddress:" + checkVerificationRequest.getStakeAddress())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var pendingUserVerification = maybePendingRequest.orElseThrow();

        if (pendingUserVerification.getStatus() == VERIFIED) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_ALREADY_VERIFIED")
                    .withDetail("User verification already verified, stakeAddress:" + checkVerificationRequest.getStakeAddress())
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

        boolean isCodeExpired = now.isAfter(pendingUserVerification.getCreatedAt().plusMinutes(validationExpirationTimeMinutes));
        if (isCodeExpired) {
                return Either.left(Problem.builder()
                        .withTitle("VERIFICATION_EXPIRED")
                        .withDetail(String.format("Verification code: %s expired for stakeAddress: %s", checkVerificationRequest.getVerificationCode(), checkVerificationRequest.getStakeAddress()))
                        .withStatus(BAD_REQUEST)
                        .build());
        }

        pendingUserVerification.setStatus(VERIFIED);
        pendingUserVerification.setUpdatedAt(now);

        return Either.right(userVerificationRepository.saveAndFlush(pendingUserVerification));
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

        var maybeUserVerification = userVerificationRepository.findCompleted(
                isVerifiedRequest.getEventId(),
                isVerifiedRequest.getStakeAddress()
        );

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
