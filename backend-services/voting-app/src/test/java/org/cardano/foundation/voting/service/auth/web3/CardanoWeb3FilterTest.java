package org.cardano.foundation.voting.service.auth.web3;

import com.bloxbean.cardano.client.util.HexUtil;
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
import static org.cardano.foundation.voting.resource.Headers.*;
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
    private ChainNetwork chainNetworkStartedOn = PREPROD;

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

    @Test
    void doFilterInternal_shouldAuthenticate_whenAllConditionsMetWithHashedContent() throws ServletException, IOException {
        chainNetworkStartedOn = MAIN;
        filter = new CardanoWeb3Filter(jsonService, expirationService, objectMapper, chainFollowerClient, chainNetworkStartedOn, loginSystemDetector);

        val payloadAsHex = "7b22616374696f6e223a224c4f47494e222c22616374696f6e54657874223a224c6f67696e222c2264617461223a7b226576656e74223a2243415244414e4f5f53554d4d49545f4157415244535f32303234222c226e6574776f726b223a224d41494e222c22726f6c65223a22564f544552222c2277616c6c65744964223a227374616b6531757970617970326e797a793636746d637a36796a757468353970796d3064663833726a706b30373538666871726e6371387663647a222c2277616c6c657454797065223a2243415244414e4f227d2c22736c6f74223a22313336303638393432227d";
        //{"action":"LOGIN","actionText":"Login","data":{"event":"CARDANO_SUMMIT_AWARDS_2024","network":"MAIN","role":"VOTER","walletId":"stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz","walletType":"CARDANO"},"slot":"136068942"}

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("136068942")
                .data(Map.of(
                        "event", "CARDANO_SUMMIT_AWARDS_2024",
                        "walletId", "stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz",
                        "walletType", WalletType.CARDANO.name(),
                        "network", "MAIN",
                        "role", "VOTER"
                        )
                )
                .build();

        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84582aa201276761646472657373581de103d205532089ad2f7816892e2ef42849b7b52788e41b3fd43a6e01cfa166686173686564f5581c1c1afc33a1ed48205eadcbbda2fc8e61442af2e04673616f21b7d0385840954858f672e9ca51975655452d79a8f106011e9535a2ebfb909f7bbcce5d10d246ae62df2da3a7790edd8f93723cbdfdffc5341d08135b1a40e7a998e8b2ed06");
        when(request.getHeader(X_Ballot_Payload)).thenReturn(payloadAsHex);
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a4010103272006215820c13745be35c2dfc3fa9523140030dda5b5346634e405662b1aae5c61389c55b3");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 512, 136068942, true, MAIN))
        );

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.right(Optional.of(mock(ChainFollowerClient.EventDetailsResponse.class))));

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz");

        val cardanoDetails = (CardanoWeb3Details) SecurityContextHolder.getContext().getAuthentication().getDetails();

        assertThat(cardanoDetails.getSignedCIP30().getSignature()).isEqualTo("84582aa201276761646472657373581de103d205532089ad2f7816892e2ef42849b7b52788e41b3fd43a6e01cfa166686173686564f5581c1c1afc33a1ed48205eadcbbda2fc8e61442af2e04673616f21b7d0385840954858f672e9ca51975655452d79a8f106011e9535a2ebfb909f7bbcce5d10d246ae62df2da3a7790edd8f93723cbdfdffc5341d08135b1a40e7a998e8b2ed06");
        assertThat(cardanoDetails.getSignedCIP30().getPublicKey()).isEqualTo(Optional.of("a4010103272006215820c13745be35c2dfc3fa9523140030dda5b5346634e405662b1aae5c61389c55b3"));
        assertThat(cardanoDetails.getWeb3CommonDetails().getAction()).isEqualTo(Web3Action.LOGIN);
        assertThat(cardanoDetails.getWeb3CommonDetails().getNetwork()).isEqualTo(MAIN);
        assertThat(cardanoDetails.getPayload()).isEqualTo(new String(HexUtil.decodeHexString(payloadAsHex)));
        assertThat(cardanoDetails.getEnvelope()).isNotNull();
    }

    // CIP30 is a data sign with hash only but hashes do not properly match
    @Test
    void doFilterInternal_shouldNotAuthenticate_whenHashesMismatch() throws IOException, ServletException {
        chainNetworkStartedOn = MAIN;
        filter = new CardanoWeb3Filter(jsonService, expirationService, objectMapper, chainFollowerClient, chainNetworkStartedOn, loginSystemDetector);

        //{"action":"LOGIN","actionText":"Login","data":{"event":"CARDANO_SUMMIT_AWARDS_2024","network":"MAIN","role":"VOTER","walletId":"stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz","walletType":"CARDANO"},"slot":"136068943"}

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("136068943")
                .data(Map.of(
                                "event", "CARDANO_SUMMIT_AWARDS_2024",
                                "walletId", "stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz",
                                "walletType", WalletType.CARDANO.name(),
                                "network", "MAIN",
                                "role", "VOTER"
                        )
                )
                .build();

        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84582aa201276761646472657373581de103d205532089ad2f7816892e2ef42849b7b52788e41b3fd43a6e01cfa166686173686564f5581c1c1afc33a1ed48205eadcbbda2fc8e61442af2e04673616f21b7d0385840954858f672e9ca51975655452d79a8f106011e9535a2ebfb909f7bbcce5d10d246ae62df2da3a7790edd8f93723cbdfdffc5341d08135b1a40e7a998e8b2ed06");
        when(request.getHeader(X_Ballot_Payload)).thenReturn("7B22616374696F6E223A224C4F47494E222C22616374696F6E54657874223A224C6F67696E222C2264617461223A7B226576656E74223A2243415244414E4F5F53554D4D49545F4157415244535F32303234222C226E6574776F726B223A224D41494E222C22726F6C65223A22564F544552222C2277616C6C65744964223A227374616B6531757970617970326E797A793636746D637A36796A757468353970796D3064663833726A706B30373538666871726E6371387663647A222C2277616C6C657454797065223A2243415244414E4F227D2C22736C6F74223A22313336303638393433227D");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a4010103272006215820c13745be35c2dfc3fa9523140030dda5b5346634e405662b1aae5c61389c55b3");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 512, 136068943, true, MAIN))
        );

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.right(Optional.of(mock(ChainFollowerClient.EventDetailsResponse.class))));

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("CIP_30_HASH_MISMATCH");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

    // CIP30 is a data sign with hash only but lets say we forgot to send the payload...
    @Test
    void doFilterInternal_shouldNotAuthenticate_whenPayloadNotSent() throws IOException, ServletException {
        chainNetworkStartedOn = MAIN;
        filter = new CardanoWeb3Filter(jsonService, expirationService, objectMapper, chainFollowerClient, chainNetworkStartedOn, loginSystemDetector);

        //{"action":"LOGIN","actionText":"Login","data":{"event":"CARDANO_SUMMIT_AWARDS_2024","network":"MAIN","role":"VOTER","walletId":"stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz","walletType":"CARDANO"},"slot":"136068943"}

        val genericEnvelope = CIP93Envelope.<Map<String, Object>>builder()
                .action("LOGIN")
                .slot("136068943")
                .data(Map.of(
                                "event", "CARDANO_SUMMIT_AWARDS_2024",
                                "walletId", "stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz",
                                "walletType", WalletType.CARDANO.name(),
                                "network", "MAIN",
                                "role", "VOTER"
                        )
                )
                .build();

        when(loginSystemDetector.detect(request)).thenReturn(Optional.of(CARDANO_CIP93));
        when(request.getHeader(X_Ballot_Signature)).thenReturn("84582aa201276761646472657373581de103d205532089ad2f7816892e2ef42849b7b52788e41b3fd43a6e01cfa166686173686564f5581c1c1afc33a1ed48205eadcbbda2fc8e61442af2e04673616f21b7d0385840954858f672e9ca51975655452d79a8f106011e9535a2ebfb909f7bbcce5d10d246ae62df2da3a7790edd8f93723cbdfdffc5341d08135b1a40e7a998e8b2ed06");
        when(request.getHeader(X_Ballot_PublicKey)).thenReturn("a4010103272006215820c13745be35c2dfc3fa9523140030dda5b5346634e405662b1aae5c61389c55b3");

        val cip30VerificationResult = mock(Cip30VerificationResult.class);
        when(cip30VerificationResult.isValid()).thenReturn(true);
        when(cip30VerificationResult.getAddress(any())).thenReturn(Optional.of("stake1uypayp2nyzy66tmcz6yjuth59pym0df83rjpk0758fhqrncq8vcdz"));

        when(chainFollowerClient.getChainTip()).thenReturn(Either.right(
                new ChainFollowerClient.ChainTipResponse("hash", 512, 136068943, true, MAIN))
        );

        when(chainFollowerClient.getEventDetails(any())).thenReturn(Either.right(Optional.of(mock(ChainFollowerClient.EventDetailsResponse.class))));

        when(jsonService.decodeGenericCIP93(any())).thenReturn(Either.right(genericEnvelope));

        filter.doFilterInternal(request, response, chain);

        val problemCaptor = ArgumentCaptor.forClass(Problem.class);

        verify(objectMapper, times(1)).writeValueAsString(problemCaptor.capture());
        val capturedProblem = problemCaptor.getValue();

        assertThat(capturedProblem.getTitle()).isEqualTo("HASHED_CONTENT_NO_PAYLOAD");
        assertThat(capturedProblem.getStatus()).isEqualTo(BAD_REQUEST);
    }

}
