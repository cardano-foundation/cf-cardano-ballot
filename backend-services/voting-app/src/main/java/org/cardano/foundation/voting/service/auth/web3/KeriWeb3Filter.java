package org.cardano.foundation.voting.service.auth.web3;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.client.KeriVerificationClient;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.web3.SignedKERI;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Enums;
import org.cardano.foundation.voting.utils.Keri;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.domain.Role.VOTER;
import static org.cardano.foundation.voting.domain.web3.WalletType.KERI;
import static org.cardano.foundation.voting.resource.Headers.*;
import static org.cardano.foundation.voting.service.auth.LoginSystem.KERI_SIGN;
import static org.cardano.foundation.voting.service.auth.web3.MoreFilters.sendBackProblem;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeriWeb3Filter extends OncePerRequestFilter {

    private final JsonService jsonService;
    private final ExpirationService expirationService;
    private final ObjectMapper objectMapper;

    // TODO can we do this via local access?
    private final ChainFollowerClient chainFollowerClient;
    private final ChainNetwork chainNetworkStartedOn;
    private final LoginSystemDetector loginSystemDetector;
    private final KeriVerificationClient keriVerificationClient;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        val logonSystemM = loginSystemDetector.detect(req);
        if (logonSystemM.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        val loginSystem = logonSystemM.orElseThrow();
        if (loginSystem != KERI_SIGN) {
            chain.doFilter(req, res);
            return;
        }

        val headerSignatureM = Optional.ofNullable(req.getHeader(X_Login_Signature));
        val headerPayloadM = Optional.ofNullable(req.getHeader(X_Login_Payload));
        val headerAidM = Optional.ofNullable(req.getHeader(X_Login_PublicKey));

        if (headerSignatureM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("NO_LOGIN_HTTP_HEADERS_SET")
                    .withDetail("X_Login_Signature http headers must be set.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        if (headerPayloadM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("NO_LOGIN_HTTP_HEADERS_SET")
                    .withDetail("X_Login_Payload http headers must be set.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        if (headerAidM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("NO_LOGIN_HTTP_HEADERS_SET")
                    .withDetail("X_Login_PublicKey http headers must be set.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val headerSignature = headerSignatureM.orElseThrow();
        val headerPayload = new String(decodeHexString(headerPayloadM.orElseThrow()));
        val headerAid = headerAidM.orElseThrow();

        val keriVerificationResultE = keriVerificationClient.verifySignature(headerAid, headerSignature, headerPayload);
        if (keriVerificationResultE.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("KERI_SIGNATURE_VERIFICATION_FAILED")
                    .withDetail("Unable to verify KERI header signature, reason: " + keriVerificationResultE.swap().get().getDetail())
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val keriEnvelopeE = jsonService.decodeGenericKeri(headerPayload);
        if (keriEnvelopeE.isEmpty()) {
            log.info("Invalid KERI envelope!");

            val problem = Problem.builder()
                    .withTitle("INVALID_KERI_ENVELOPE")
                    .withDetail("KERI envelope decoding failed.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);

            return;
        }

        val genericEnvelope = keriEnvelopeE.get();

        val envelopeWalletIdM = Optional.ofNullable((String) genericEnvelope.getData().get("walletId"));
        if (envelopeWalletIdM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("WALLET_ID_NOT_FOUND")
                    .withDetail("WalletId not found in the KERI envelope.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val envelopeWalletId = envelopeWalletIdM.orElseThrow();

        val envelopeWalletTypeM = Enums.getIfPresent(WalletType.class, (String) genericEnvelope.getData().get("walletType"));
        if (envelopeWalletTypeM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("INVALID_WALLET_TYPE")
                    .withDetail("Invalid wallet type, supported wallet types:" + Arrays.asList(WalletType.values()))
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val envelopeWalletType = envelopeWalletTypeM.get();

        if (envelopeWalletType != KERI) {
            val problem = Problem.builder()
                    .withTitle("INVALID_WALLET_TYPE")
                    .withDetail("Invalid wallet type, supported wallet types:" + Arrays.asList(WalletType.values()))
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val envelopeWebActionM = genericEnvelope.getActionAsEnum();
        if (envelopeWebActionM.isEmpty()) {
            log.info("Unrecognised action, action:{}", genericEnvelope.getAction());

            val problem = Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val web3Action = envelopeWebActionM.orElseThrow();

        val slotStr = genericEnvelope.getSlot();

        if (!isNumeric(slotStr)) {
            val problem = Problem.builder()
                    .withTitle("INVALID_SLOT")
                    .withDetail("KERI envelope slot is not numeric!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val chainTipE = chainFollowerClient.getChainTip();
        if (chainTipE.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("CHAIN_TIP_ERROR")
                    .withDetail("Unable to get chain tip from chain-tip follower service, reason: " + chainTipE.swap().get().getDetail())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val chainTip = chainTipE.get();

        val slotE = genericEnvelope.getSlotAsLong();
        if (slotE.isEmpty()) {
            sendBackProblem(objectMapper, res, slotE.swap().get());
            return;
        }
        val slot = slotE.get();

        val slotExpired = expirationService.isSlotExpired(chainTip, slot);
        if (slotExpired) {
            val problem = Problem.builder()
                    .withTitle("EXPIRED_SLOT")
                    .withDetail("Slot has expired!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val envelopeEventId = (String) genericEnvelope.getData().get("event");
        val envelopeNetworkString = (String) genericEnvelope.getData().get("network");

        val networkM = Enums.getIfPresent(ChainNetwork.class, envelopeNetworkString);
        if (networkM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid chainNetwork, supported networks:" + ChainNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val chainNetwork = networkM.get();

        if (chainNetwork != chainNetworkStartedOn) {
            log.warn("Invalid chainNetwork, network:{}", envelopeNetworkString);

            val problem = Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid chainNetwork, backed configured with chainNetwork:" + chainNetworkStartedOn + ", however request is with chainNetwork:" + chainNetwork)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val keriE = Keri.checkAid(headerAid);
        if (keriE.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("INVALID_KERI_AID")
                    .withDetail("Invalid KERI Aid, reason: " + keriE.swap().get().getDetail())
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        if (!headerAid.equals(envelopeWalletId)) {
            val problem = Problem.builder()
                    .withTitle("AID_MISMATCH")
                    .withDetail("Aid mismatch, KERI signed address:" + headerAid + ", however request is with walletId:" + envelopeWalletId)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventDetailsE = chainFollowerClient.getEventDetails(envelopeEventId);
        if (eventDetailsE.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + envelopeEventId)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventDetailsM = eventDetailsE.get();
        if (eventDetailsM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Event not found, id: " + envelopeEventId)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventDetails = eventDetailsM.orElseThrow();

        val signedKERI = new SignedKERI(headerSignature, headerPayload, headerAid);

        val web3Details = Web3CommonDetails.builder()
                .event(eventDetails)
                .walletType(KERI)
                .walletId(envelopeWalletId)
                .chainTip(chainTip)
                .action(web3Action)
                .network(chainNetwork)
                .build();

        val keriDetails = KeriWeb3Details.builder()
                .web3CommonDetails(web3Details)
                .signedKERI(signedKERI)
                .envelope(genericEnvelope)
                .build();

        val authentication = new Web3AuthenticationToken(keriDetails, List.of(new SimpleGrantedAuthority(VOTER.name())));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

}
