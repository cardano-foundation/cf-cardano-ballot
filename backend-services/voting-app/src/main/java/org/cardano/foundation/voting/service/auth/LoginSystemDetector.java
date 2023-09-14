package org.cardano.foundation.voting.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static org.cardano.foundation.voting.service.auth.LoginSystem.CIP93;
import static org.cardano.foundation.voting.service.auth.LoginSystem.JWT;

@Component
public class LoginSystemDetector {

    public Optional<LoginSystem> detect(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTHORIZATION);
        var cip93SignatureHeader = request.getHeader("X-CIP93-Signature");

        if (!isEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(JWT);
        }

        if (!isEmpty(cip93SignatureHeader)) {
            return Optional.of(CIP93);
        }

        return Optional.empty();
    }

}
