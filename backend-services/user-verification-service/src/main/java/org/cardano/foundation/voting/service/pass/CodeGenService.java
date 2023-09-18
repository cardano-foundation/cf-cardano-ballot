package org.cardano.foundation.voting.service.pass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
public class CodeGenService {

    private final static SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateRandomCode() {
        var randomVerificationCode = SECURE_RANDOM.nextInt(100_000, 999_999);

        return String.valueOf(randomVerificationCode);
    }

}
