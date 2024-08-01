package org.cardano.foundation.voting.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cardano.foundation.voting.resource.Headers.*;
import static org.mockito.Mockito.when;

class LoginSystemDetectorTest {

    private LoginSystemDetector loginSystemDetector;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        loginSystemDetector = new LoginSystemDetector();
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void detect_shouldReturnJWT_whenAuthHeaderStartsWithBearer() {
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer token");

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).contains(LoginSystem.JWT);
    }

    @Test
    void detect_shouldReturnCardanoCIP93_whenXLoginSignatureIsPresentAndWalletTypeIsCardano() {
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Wallet_Type)).thenReturn(WalletType.CARDANO.name());

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).contains(LoginSystem.CARDANO_CIP93);
    }

    @Test
    void detect_shouldReturnKeriSign_whenXLoginSignatureAndXLoginPayloadArePresentAndWalletTypeIsKeri() {
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
        when(request.getHeader(X_Wallet_Type)).thenReturn(WalletType.KERI.name());

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).contains(LoginSystem.KERI_SIGN);
    }

    @Test
    void detect_shouldReturnEmpty_whenXWalletTypeIsEmpty() {
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getHeader(X_Wallet_Type)).thenReturn(null);

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).isEmpty();
    }

    @Test
    void detect_shouldReturnEmpty_whenXLoginSignatureIsAbsentAndWalletTypeIsCardano() {
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getHeader(X_Wallet_Type)).thenReturn(WalletType.CARDANO.name());

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).isEmpty();
    }

    @Test
    void detect_shouldReturnEmpty_whenXLoginSignatureIsAbsentAndXLoginPayloadIsAbsentAndWalletTypeIsKeri() {
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getHeader(X_Wallet_Type)).thenReturn(WalletType.KERI.name());

        Optional<LoginSystem> result = loginSystemDetector.detect(request);

        assertThat(result).isEmpty();
    }

}
