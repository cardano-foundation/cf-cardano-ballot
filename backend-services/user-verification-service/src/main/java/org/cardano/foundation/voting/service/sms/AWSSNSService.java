package org.cardano.foundation.voting.service.sms;

import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.client.AWSSNSClient;
import org.zalando.problem.Problem;

@RequiredArgsConstructor
public class AWSSNSService implements SMSService {

    private final AWSSNSClient awssnsClient;

    @Override
    public Either<Problem, SMSVerificationResponse> publishTextMessage(String message,
                                                                       Phonenumber.PhoneNumber phoneNumber) {
        return awssnsClient.publishTextMessage(message, phoneNumber)
                .map(publishResponse -> new SMSVerificationResponse(publishResponse.messageId()));
    }

}
