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
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardano.foundation.voting.service.auth.LoginSystem;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardanofoundation.cip30.Cip30VerificationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cardano.foundation.voting.domain.ChainNetwork.*;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_PublicKey;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_Signature;
import static org.cardano.foundation.voting.service.auth.LoginSystem.CARDANO_CIP93;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.zalando.problem.Status.*;

class CardanoWeb3FilterTest {

    private CardanoWeb3Filter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private ObjectMapper objectMapper;
    private JsonService jsonService;
    private ExpirationService expirationService;
    private ChainFollowerClient chainFollowerClient;
    private LoginSystemDetector loginSystemDetector;

    private final ChainNetwork chainNetworkStartedOn = PREPROD;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = mock(ObjectMapper.class);
        jsonService = mock(JsonService.class);
        expirationService = mock(ExpirationService.class);
        chainFollowerClient = mock(ChainFollowerClient.class);
        loginSystemDetector = mock(LoginSystemDetector.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);

        filter = new CardanoWeb3Filter(jsonService, expirationService, objectMapper, chainFollowerClient, chainNetworkStartedOn, loginSystemDetector);
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
    void doFilterInternal_shouldContinueChain_whenLoginSystemIsNotCardanoCIP93() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(LoginSystem.JWT));

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenHeaderSignatureIsMissing() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());

        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("NO_LOGIN_HTTP_HEADERS_SET");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenHeaderPublicKeyIsMissing() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());

        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_CIP30_DATA_SIGNATURE");
        assertThat(capturedProblem.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenCIP30VerificationFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(false);
        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.left(mock(Problem.class)));

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenAddressNotFound() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.empty());
        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.left(mock(Problem.class)));

        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(any(Problem.class));
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenCIP93EnvelopeDecodingFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));
        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.left(mock(Problem.class)));

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());

        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_CIP93_ENVELOPE");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenWalletTypeIsInvalid() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("123")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", "INVALID")
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);
        filter.doFilterInternal(request, response, chain);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("MISSING_WALLET_TYPE");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenSlotIsNotNumeric() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("notNumeric")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_SLOT");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnInternalServerError_whenChainTipFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.left(Problem.builder()
                .withTitle("CHAIN_TIP_NOT_FOUND")
                .withDetail("Unable to get chain tip from backend service.")
                .withStatus(INTERNAL_SERVER_ERROR)
                .build())
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("123")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();
        assertThat(capturedProblem.getTitle()).isEqualTo("CHAIN_TIP_ERROR");
        assertThat(capturedProblem.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenSlotIsExpired() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        when(expirationService.isSlotExpired(any(), anyLong())).thenReturn(true);

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("EXPIRED_SLOT");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenNetworkIsInvalid() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", WalletType.CARDANO.name(),
                        "network", "DOES_NOT_EXIST")
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("INVALID_NETWORK");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenChainNetworkMismatch() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, PREPROD))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "walletId",
                        "walletType", WalletType.CARDANO.name(),
                        "network", DEV.name())
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("NETWORK_MISMATCH");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenStakeAddressMismatch() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901a90a2020202020202020202020207b0a202020202020202020202020202022616374696f6e223a20224c4f47494e222c0a202020202020202020202020202022616374696f6e54657874223a20224c6f67696e222c0a202020202020202020202020202022736c6f74223a20223233393432333439222c0a20202020202020202020202020202264617461223a207b0a202020202020202020202020202020202277616c6c65744964223a20227374616b655f74657374317571383430786179793078637873796d7a6d6d7870706b35353668666c6b7377326d6e6b65796472613465756b7971397774343932222c0a202020202020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a20202020202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a20202020202020202020202020202020226e6574776f726b223a202250524550524f44222c0a2020202020202020202020202020202022726f6c65223a2022564f544552220a20202020202020202020202020207d0a2020202020202020202020207d0a5840035d5e84302fc2f8530c80964c34fc0576631196dd3d82151514c2e2aad91e6473aa11ef74ddb561b2ade04ed2bb7f32b8daf5e90279747c89f1136e1a4e6d0e");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, PREPROD))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "stake_test1uq840xayy0xcxsymzmmxppk556hflksw2mnkeydra4eukyq9wt492",
                        "walletType", WalletType.CARDANO.name(),
                        "event", "CF_TEST_EVENT_01",
                        "network", PREPROD.name(),
                        "role", "VOTER"
                        )
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("STAKE_ADDRESS_MISMATCH");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenStakeAddressNetworkMismatch() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de1f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de1f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a584003cb839f7e4bae85b9b4844521c8ea52501f163ba6dc1075f86eef669bafac01802aa1cdfc3aee03351b30a4d8a00f788d15e598eeb83fdba96b1aa2d471500e");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de1f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake1vpu5vlrf4xkxv2qpwngf6cjhtw542ayty80v8dyr49rf5egfu2p0u"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "stake_test1uq840xayy0xcxsymzmmxppk556hflksw2mnkeydra4eukyq9wt492",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("STAKE_ADDRESS_NETWORK_MISMATCH");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldReturnInternalServerError_whenEventDetailsFails() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("walletId"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.left(Problem.builder()
                .withTitle("EVENT_DETAILS_ERROR")
                .withDetail("Unable to get event details from backend service.")
                .withStatus(INTERNAL_SERVER_ERROR)
                .build())
        );

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("ERROR_GETTING_EVENT_DETAILS");
        assertThat(capturedProblem.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    void doFilterInternal_shouldReturnBadRequest_whenEventDetailsNotFound() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, PREPROD))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                        "walletType", WalletType.CARDANO.name(),
                        "network", PREPROD.name())
                )
                .build();

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.right(Optional.empty()));

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);
        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("UNRECOGNISED_EVENT");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenAllConditionsMet() throws ServletException, IOException {
        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 500, 23942349L, true, chainNetworkStartedOn))
        );

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("23942349")
                .data(Map.of(
                        "walletId", "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                        "walletType", WalletType.CARDANO.name(),
                        "network", chainNetworkStartedOn.name())
                )
                .build();

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.right(Optional.of(mock(ChainFollowerClient.EventDetailsResponse.class))));

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6");

        val cardanoDetails = (CardanoWeb3Details) SecurityContextHolder.getContext().getAuthentication().getDetails();

        assertThat(cardanoDetails.getSignedCIP30().getSignature()).isEqualTo("84584aa3012704581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c6761646472657373581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3ca166686173686564f45901aa0a20202020202020207b0a20202020202020202020202022616374696f6e223a2022564945575f564f54455f52454345495054222c0a20202020202020202020202022616374696f6e54657874223a20225669657720566f74652052656365697074222c0a20202020202020202020202022736c6f74223a20223430323632343036222c0a2020202020202020202020202264617461223a207b0a2020202020202020202020202277616c6c65744964223a20227374616b655f74657374317572757736777377616738307364306c3537616c65686a34376c6c663674783936343032767438766b7334366b30713065326e6536222c0a2020202020202020202020202277616c6c657454797065223a202243415244414e4f222c0a202020202020202020202020226576656e74223a202243465f544553545f4556454e545f3031222c0a2020202020202020202020202263617465676f7279223a20224348414e47455f534f4d455448494e47222c0a202020202020202020202020226e6574776f726b223a202250524550524f44220a2020202020202020202020207d0a20202020202020207d0a5840f5146790954eaae53d521252d36edf9015c896ed3a08b5448599d593802a9268040f6ce7a21ec24d8afc3e39f419e63c8827237126bf1dd1f1cf57b92310b805");
        assertThat(cardanoDetails.getSignedCIP30().getPublicKey()).isEqualTo(Optional.of("a5010102581de0f8ed3a0eea0ef835ffa7bbfcde55f7fe9d2cc5d55ea62cecb42bab3c032720062158202b41abe97d5c84f30691740cb564305fd3f30514777e56581fd0bef02a92e29d"));
        assertThat(cardanoDetails.getWeb3CommonDetails().getAction()).isEqualTo(Web3Action.LOGIN);
        assertThat(cardanoDetails.getWeb3CommonDetails().getNetwork()).isEqualTo(chainNetworkStartedOn);
        assertThat(cardanoDetails.getEnvelope()).isNotNull();
    }

}
