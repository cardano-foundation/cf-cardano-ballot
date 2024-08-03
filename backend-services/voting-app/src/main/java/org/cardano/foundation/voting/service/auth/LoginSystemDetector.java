package org.cardano.foundation.voting.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static org.cardano.foundation.voting.domain.web3.WalletType.CARDANO;
import static org.cardano.foundation.voting.resource.Headers.*;
import static org.cardano.foundation.voting.service.auth.LoginSystem.CARDANO_CIP93;
import static org.cardano.foundation.voting.service.auth.LoginSystem.JWT;

@Component
public class LoginSystemDetector {

    public Optional<LoginSystem> detect(HttpServletRequest request) {
        val authHeader = request.getHeader(AUTHORIZATION);

        if (!isEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(JWT);
        }

        @Nullable
        val xLoginSignature = request.getHeader(X_Login_Signature);

        @Nullable
        val xLoginPayload = request.getHeader(X_Login_Payload);

        val xWalletTypeM = Enums.getIfPresent(WalletType.class, request.getHeader(X_Wallet_Type));

        if (xWalletTypeM.isEmpty()) {
            return Optional.empty();
        }

        val xWalletType = xWalletTypeM.orElseThrow();

        if (!isEmpty(xLoginSignature) && xWalletType == CARDANO) {
            return Optional.of(CARDANO_CIP93);
        }

        if (!isEmpty(xLoginSignature) && !isEmpty(xLoginPayload) && xWalletType == WalletType.KERI) {
            return Optional.of(LoginSystem.KERI_SIGN);
        }

        return Optional.empty();
    }

}
