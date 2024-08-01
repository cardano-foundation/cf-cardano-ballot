package org.cardano.foundation.voting.service.auth.web3;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.KeriVerificationClient;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.web3.KERIEnvelope;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.service.auth.LoginSystem;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cardano.foundation.voting.domain.ChainNetwork.MAIN;
import static org.cardano.foundation.voting.resource.Headers.*;
import static org.cardano.foundation.voting.service.auth.LoginSystem.KERI_SIGN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.zalando.problem.Status.BAD_REQUEST;

class KeriWeb3FilterTest {

    private KeriWeb3Filter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private ObjectMapper objectMapper;
    private JsonService jsonService;
    private ExpirationService expirationService;
    private ChainFollowerClient chainFollowerClient;
    private LoginSystemDetector loginSystemDetector;
    private KeriVerificationClient keriVerificationClient;

    @Captor
    ArgumentCaptor<Problem> problemCaptor;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = mock(ObjectMapper.class);
        jsonService = mock(JsonService.class);
        expirationService = mock(ExpirationService.class);
        chainFollowerClient = mock(ChainFollowerClient.class);
        loginSystemDetector = mock(LoginSystemDetector.class);
        keriVerificationClient = mock(KeriVerificationClient.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);

        ChainNetwork chainNetworkStartedOn = MAIN;  // Example initialization

        filter = new KeriWeb3Filter(jsonService, expirationService, objectMapper, chainFollowerClient, chainNetworkStartedOn, loginSystemDetector, keriVerificationClient);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenLoginSystemIsNotDetected() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenLoginSystemIsNotKeriSign() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(LoginSystem.JWT));

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenHeaderSignatureIsMissing() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenHeaderPayloadIsMissing() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenHeaderAidIsMissing() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("payload");

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenKeriVerificationFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("7061796C6F6164");
        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
        when(keriVerificationClient.verifySignature(any(), any(), any())).thenReturn(Either.left(mock(Problem.class)));

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenKeriEnvelopeDecodingFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("7061796C6F6164");
        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
        when(keriVerificationClient.verifySignature(any(), any(), any())).thenReturn(Either.right(true));
        when(jsonService.decodeGenericKeri(any())).thenReturn(Either.left(mock(Problem.class)));

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_KERI_ENVELOPE");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenWalletIdIsMissingInEnvelope() throws ServletException, IOException {
        // Setup mocks and stubs for successful KERI verification and decoding
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("7061796C6F6164");
        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");

        when(keriVerificationClient.verifySignature(any(), any(), any())).thenReturn(Either.right(true));

        val genericEnvelope = KERIEnvelope.<Map<String, Object>>builder()
                .uri("uri")
                .action("LOGIN")
                .slot(String.valueOf(123L))
                .data(Map.of("walletType", WalletType.KERI.name()))
                .build();

        when(jsonService.decodeGenericKeri(any())).thenReturn(Either.right(genericEnvelope));

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("WALLET_ID_NOT_FOUND");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenInvalidWalletType() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("7061796C6F6164");
        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");

        when(keriVerificationClient.verifySignature(any(), any(), any())).thenReturn(Either.right(true));

        val genericEnvelope = KERIEnvelope.<Map<String, Object>>builder()
                .uri("uri")
                .action("LOGIN")
                .slot(String.valueOf(123L))
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", "INVALID")
                )
                .build();

        when(jsonService.decodeGenericKeri(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_WALLET_TYPE");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenSlotIsNotNumeric() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
        when(request.getHeader(X_Login_Payload)).thenReturn("7061796C6F6164");
        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");

        when(keriVerificationClient.verifySignature(any(), any(), any())).thenReturn(Either.right(true));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, MAIN))
        );

        val genericEnvelope = KERIEnvelope.<Map<String, Object>>builder()
                .uri("uri")
                .action("LOGIN")
                .slot("notNumeric")
                .data(Map.of(
                        "walletId", "EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO",
                        "walletType", WalletType.KERI.name(),
                        "slot", "notNumeric",
                        "network", MAIN.name())
                )
                .build();

        when(jsonService.decodeGenericKeri(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_SLOT");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

//    @Test
//    void doFilterInternal_shouldReturnInternalServerError_whenChainTipFails() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.empty());
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }

//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenSlotIsExpired() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(true);
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenNetworkIsInvalid() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "INVALID"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenChainNetworkMismatch() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "TESTNET"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenAidCheckFails() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "MAINNET"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        when(Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO")).thenReturn(Optional.empty());
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenAidMismatch() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "differentWalletId", "walletType", "KERI", "network", "MAINNET"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        when(Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO")).thenReturn(Optional.of(mock(Problem.class)));
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnInternalServerError_whenEventDetailsFails() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "MAINNET", "event", "eventId"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        when(Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO")).thenReturn(Optional.of(mock(Problem.class)));
//
//        when(chainFollowerClient.getEventDetails("eventId")).thenReturn(Optional.empty());
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldReturnBadRequest_whenEventDetailsNotFound() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "MAINNET", "event", "eventId"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        when(Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO")).thenReturn(Optional.of(mock(Problem.class)));
//
//        val eventDetails = mock(EventDetails.class);
//        when(chainFollowerClient.getEventDetails("eventId")).thenReturn(Optional.of(Optional.empty()));
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(objectMapper, times(1)).writeValue(any(), any(Problem.class));
//    }
//
//    @Test
//    void doFilterInternal_shouldAuthenticate_whenAllConditionsMet() throws ServletException, IOException {
//        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(KERI_SIGN));
//        when(request.getHeader(X_Login_Signature)).thenReturn("signature");
//        when(request.getHeader(X_Login_Payload)).thenReturn("payload");
//        when(request.getHeader(X_Login_PublicKey)).thenReturn("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO");
//
//        val genericEnvelope = mock(GenericEnvelope.class);
//        when(jsonService.decodeGenericKeri(any())).thenReturn(Optional.of(genericEnvelope));
//        when(genericEnvelope.getData()).thenReturn(Map.of("walletId", "walletId", "walletType", "KERI", "network", "MAINNET", "event", "eventId"));
//        when(genericEnvelope.getSlot()).thenReturn("123");
//
//        val chainTip = mock(ChainTip.class);
//        when(chainFollowerClient.getChainTip()).thenReturn(Optional.of(chainTip));
//        when(expirationService.isSlotExpired(chainTip, 123L)).thenReturn(false);
//
//        when(Keri.checkAid("EIA1PcKQkcW6mvs2kVwVpvaf6SMuBHLMCrx57WPW6UPO")).thenReturn(Optional.of(mock(Problem.class)));
//
//        val eventDetails = mock(EventDetails.class);
//        when(chainFollowerClient.getEventDetails("eventId")).thenReturn(Optional.of(Optional.of(eventDetails)));
//
//        filter.doFilterInternal(request, response, chain);
//
//        verify(chain, times(1)).doFilter(request, response);
//        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
//    }

}
