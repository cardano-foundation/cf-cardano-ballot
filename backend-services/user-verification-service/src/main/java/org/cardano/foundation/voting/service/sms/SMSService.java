package org.cardano.foundation.voting.service.sms;

import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import org.zalando.problem.Problem;

public interface SMSService {

    Either<Problem, SMSVerificationResponse> publishTextMessage(String message, Phonenumber.PhoneNumber phoneNumber);

}
