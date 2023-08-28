package org.cardano.foundation.voting.service.verify;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;

@Service
@Slf4j
public class TwilioVerificationServiceGateway {

    private final static String SMS_CHANNEL = "sms";

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.verify.service.sid}")
    private String verifyServiceSid;

    @Value("${twilio.edge:#{null}}")
    private Optional<String> twilioEdge = Optional.empty();

    @Value("${twilio.region:#{null}}")
    private Optional<String> twilioRegion = Optional.empty();

    @PostConstruct
    public void init() {
        log.info("Twilio account init...");
        Twilio.setAccountSid(twilioAccountSid);
        Twilio.setPassword(twilioAuthToken);

        twilioEdge.ifPresent(Twilio::setEdge);
        twilioRegion.ifPresent(Twilio::setRegion);
    }

    public Verification startVerification(Phonenumber.PhoneNumber phone) {
        return Verification.creator(
                        verifyServiceSid,
                        PhoneNumberUtil.getInstance().format(phone, INTERNATIONAL),
                        SMS_CHANNEL).create();
    }

    public VerificationCheck checkVerification(Phonenumber.PhoneNumber phone, String secretCode) {
        return VerificationCheck.creator(verifyServiceSid)
                .setTo(PhoneNumberUtil.getInstance().format(phone, INTERNATIONAL))
                .setCode(secretCode)
                .create();
    }

}
