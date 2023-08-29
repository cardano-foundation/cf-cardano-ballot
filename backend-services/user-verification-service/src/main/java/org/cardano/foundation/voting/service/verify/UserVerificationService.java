package org.cardano.foundation.voting.service.verify;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CheckVerificationRequest;
import org.cardano.foundation.voting.domain.IsVerifiedRequest;
import org.cardano.foundation.voting.domain.IsVerifiedResponse;
import org.cardano.foundation.voting.domain.StartVerificationRequest;
import org.cardano.foundation.voting.domain.entity.UserVerification;
import org.cardano.foundation.voting.repository.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.cardano.foundation.voting.domain.entity.UserVerification.Channel.SMS;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Provider.TWILIO;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Status.PENDING;
import static org.cardano.foundation.voting.domain.entity.UserVerification.Status.VERIFIED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerificationService {

    private final ChainFollowerClient chainFollowerClient;

    private final TwilioVerificationServiceGateway twilioVerificationServiceGateway;

    private final UserVerificationRepository userVerificationRepository;

    @Value("${twilio.max.send.code.attempts:25}")
    private final int maxSendCodeAttempts;

    @Transactional
    public Either<Problem, UserVerification> startVerification(StartVerificationRequest startVerificationRequest) {
        var activeEventE = chainFollowerClient.findActiveEvent(startVerificationRequest.getEventId());

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

        var twilioVerification = twilioVerificationServiceGateway.startVerification(phoneNum);
        log.info("twilioVerification response object:{}", twilioVerification);

        int sendCodeAttempts = twilioVerification.getSendCodeAttempts().size();
        if (sendCodeAttempts > maxSendCodeAttempts) {
            return Either.left(Problem.builder()
                    .withTitle("MAX_SEND_CODE_ATTEMPTS_REACHED")
                    .withDetail("Max send code attempts reached, phone number:" + startVerificationRequest.getPhoneNumber())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var userVerification = UserVerification.builder()
                .eventId(startVerificationRequest.getEventId())
                .channel(SMS)
                .provider(TWILIO)
                .status(PENDING)
                .stakeAddress(startVerificationRequest.getStakeAddress())
                .phoneNumber(startVerificationRequest.getPhoneNumber())
                .createdAt(twilioVerification.getDateCreated().toLocalDateTime())
                .updatedAt(twilioVerification.getDateUpdated().toLocalDateTime())
                .build();

        return Either.right(userVerificationRepository.save(userVerification));
    }

    @Transactional
    public Either<Problem, UserVerification> checkVerification(CheckVerificationRequest checkVerificationRequest) {
        var activeEventE = chainFollowerClient.findActiveEvent(checkVerificationRequest.getEventId());

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

        var maybePhoneNum = isValidNumber(checkVerificationRequest.getPhoneNumber());

        if (maybePhoneNum.isEmpty()) {
            log.error("Invalid phone number:{}", checkVerificationRequest.getPhoneNumber());

            return Either.left(Problem.builder()
                    .withTitle("INVALID_PHONE_NUMBER")
                    .withDetail("Invalid phone number, phone number:" + checkVerificationRequest.getPhoneNumber())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var phoneNum = maybePhoneNum.orElseThrow();

        var maybeUserVerification = userVerificationRepository.findById(checkVerificationRequest.getStakeAddress());

        if (maybeUserVerification.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_NOT_FOUND")
                    .withDetail("User verification not found, stakeAddress:" + checkVerificationRequest.getStakeAddress())
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        var userVerification = maybeUserVerification.orElseThrow();

        if (userVerification.getStatus() == VERIFIED) {
            return Either.left(Problem.builder()
                    .withTitle("USER_VERIFICATION_ALREADY_VERIFIED")
                    .withDetail("User verification already verified, stakeAddress:" + checkVerificationRequest.getStakeAddress())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var verificationCheck = twilioVerificationServiceGateway.checkVerification(phoneNum, checkVerificationRequest.getVerificationCode());
        var isApproved = Optional.ofNullable(verificationCheck.getStatus()).map(status -> status.equalsIgnoreCase("APPROVED")).orElse(false);

        if (isApproved) {
            userVerification.setStatus(VERIFIED);
            userVerification.setUpdatedAt(verificationCheck.getDateUpdated().toLocalDateTime());
            userVerification.setPhoneNumber(Optional.empty()); // we do not want to store phone number, only temporary for the provider

            return Either.right(userVerificationRepository.save(userVerification));
        }

        return Either.left(Problem.builder()
                .withTitle("INVALID_VERIFICATION_CODE")
                .withDetail("Invalid verification code, verificationCode:" + checkVerificationRequest.getVerificationCode())
                .withStatus(BAD_REQUEST)
                .build());
    }

    @Transactional
    public Either<Problem, IsVerifiedResponse> isVerified(IsVerifiedRequest isVerifiedRequest) {
        var activeEventE = chainFollowerClient.findActiveEvent(isVerifiedRequest.getEventId());

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

        var maybeUserVerification = userVerificationRepository.findById(isVerifiedRequest.getStakeAddress());

        if (maybeUserVerification.isEmpty()) {
            log.warn("Completed or pending user verification not found for:{}", isVerifiedRequest.getStakeAddress());

            return Either.right(new IsVerifiedResponse(false));
        }

        var userVerification = maybeUserVerification.orElseThrow();

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
