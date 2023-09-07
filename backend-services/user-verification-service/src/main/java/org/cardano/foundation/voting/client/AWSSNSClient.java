package org.cardano.foundation.voting.client;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.Map;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Component
@Slf4j
public class AWSSNSClient {

    @Autowired
    private SnsClient snsClient;

    public Either<Problem, PublishResponse> publishTextMessage(String text, Phonenumber.PhoneNumber phoneNumber) {
        try {
            var formattedPhone = PhoneNumberUtil.getInstance().format(phoneNumber, INTERNATIONAL);

            Map<String, MessageAttributeValue> messageAttributes = Map.of(
                    "AWS.SNS.SMS.SMSType", MessageAttributeValue.builder().stringValue("Transactional").dataType("String").build()
                    //"AWS.SNS.SMS.SenderID", MessageAttributeValue.builder().stringValue(???).build()
            );
            var request = PublishRequest.builder()
                    .message(text)
                    .messageAttributes(messageAttributes)
                    .phoneNumber(formattedPhone)
                    .build();

            return Either.right(snsClient.publish(request));
        } catch (SnsException e) {
            log.error("Unable to send SMS, reason:{}", e.getMessage());

            var awsErrorDetails = e.awsErrorDetails();

            return Either.left(Problem.builder()
                    .withTitle("SMS_SEND_ERROR")
                    .withDetail(String.format("Unable to send SMS, reason:%s, errorCode:%s", awsErrorDetails.errorMessage(), awsErrorDetails.errorCode()))
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

}
