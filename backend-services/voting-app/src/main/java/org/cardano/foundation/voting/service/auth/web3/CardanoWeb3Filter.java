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
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.web3.SignedCIP30;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Addresses;
import org.cardano.foundation.voting.utils.Enums;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.MessageFormat;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.Role.VOTER;
import static org.cardano.foundation.voting.domain.web3.WalletType.CARDANO;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_PublicKey;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_Signature;
import static org.cardano.foundation.voting.service.auth.LoginSystem.CARDANO_CIP93;
import static org.cardano.foundation.voting.service.auth.web3.MoreFilters.sendBackProblem;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.zalando.problem.Status.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardanoWeb3Filter extends OncePerRequestFilter {

    private final JsonService jsonService;

    private final ExpirationService expirationService;

    private final ObjectMapper objectMapper;

    // TODO can we do this via local access?
    private final ChainFollowerClient chainFollowerClient;

    private final ChainNetwork chainNetworkStartedOn;

    private final LoginSystemDetector loginSystemDetector;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        val maybeLoginSystem = loginSystemDetector.detect(req);

        if (maybeLoginSystem.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        val loginSystem = maybeLoginSystem.orElseThrow();

        if (loginSystem != CARDANO_CIP93) {
            chain.doFilter(req, res);
            return;
        }

        val signatureM = Optional.ofNullable(req.getHeader(X_Ballot_Signature));
        val publicKey = req.getHeader(X_Ballot_PublicKey);

        if (signatureM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("NO_LOGIN_HTTP_HEADERS_SET")
                    .withDetail("LOGIN http headers must be set.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val signature = signatureM.orElseThrow();
        val signedWeb3Request = new SignedCIP30(signature, Optional.ofNullable(publicKey));

        val cip30Verifier = new CIP30Verifier(signedWeb3Request.getSignature(), signedWeb3Request.getPublicKey());
        val cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.info("Unable to decode valid signed web3 request!");

            val problem = Problem.builder()
                    .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                    .withDetail("Invalid CIP93 cose signature!")
                    .withStatus(UNAUTHORIZED)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val maybeAddress = cipVerificationResult.getAddress(AddressFormat.TEXT);

        if (maybeAddress.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("ADDRESS_NOT_FOUND")
                    .withDetail("Bech32 address not found in the signed data.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val walletId = maybeAddress.orElseThrow();

        val cipBody = cipVerificationResult.getMessage(MessageFormat.TEXT);

        val cip93EnvelopeE = jsonService.decodeGenericCIP93(cipBody);
        if (cip93EnvelopeE.isEmpty()) {
            log.info("Invalid CIP-93 envelope!");

            val problem = Problem.builder()
                    .withTitle("INVALID_CIP93_ENVELOPE")
                    .withDetail("CIP-93 envelope decoding failed.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);

            return;
        }

        val genericEnvelope = cip93EnvelopeE.get();

        val maybeWeb3Action = genericEnvelope.getActionAsEnum();
        if (maybeWeb3Action.isEmpty()) {
            log.info("Unrecognised action, action:{}", genericEnvelope.getAction());

            val problem = Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val web3Action = maybeWeb3Action.orElseThrow();

        val slotStr = genericEnvelope.getSlot();

        if (!isNumeric(slotStr)) {
            val problem = Problem.builder()
                    .withTitle("INVALID_SLOT")
                    .withDetail("CIP-93 envelope slot is not numeric!")
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
        if (slotE.isLeft()) {
            val problem = slotE.getLeft();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val slot = slotE.get();

        val slotExpired = expirationService.isSlotExpired(chainTip, slot);

        if (slotExpired) {
            val problem = Problem.builder()
                    .withTitle("EXPIRED_SLOT")
                    .withDetail("CIP-93 envelope slot expired!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventId = (String) genericEnvelope.getData().get("event");
        val networkString = (String) genericEnvelope.getData().get("network");
        val envelopeWalletId = (String) genericEnvelope.getData().get("walletId");
        val envelopeWalletTypeM = Enums.getIfPresent(WalletType.class, (String) genericEnvelope.getData().get("walletType"));

        if (envelopeWalletTypeM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("MISSING_WALLET_TYPE")
                    .withDetail("CIP-93 envelope walletType not found!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }
        val envelopeWalletType = envelopeWalletTypeM.orElseThrow();

        if (envelopeWalletType != CARDANO) {
            val problem = Problem.builder()
                    .withTitle("MUST_BE_CARDANO_WALLET")
                    .withDetail("Only Cardano wallet type supported for CIP-93 envelopes!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val networkM = Enums.getIfPresent(ChainNetwork.class, networkString);
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
            log.warn("Invalid chainNetwork, network:{}", networkString);

            val problem = Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid chainNetwork, backed configured with chainNetwork:" + chainNetworkStartedOn + ", however request is with chainNetwork:" + chainNetwork)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val walletIdE = Addresses.checkWalletId(chainNetwork, CARDANO, walletId);
        if (walletIdE.isEmpty()) {
            val problem = walletIdE.getLeft();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        if (!walletId.equals(envelopeWalletId)) {
            val problem = Problem.builder()
                    .withTitle("STAKE_ADDRESS_MISMATCH")
                    .withDetail("Stake address mismatch, CIP-93 signed address:" + walletId + ", however request is with address:" + envelopeWalletId)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }


        val eventDetailsE = chainFollowerClient.getEventDetails(eventId);
        if (eventDetailsE.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + eventId)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventDetailsM = eventDetailsE.get();
        if (eventDetailsM.isEmpty()) {
            val problem = Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Event not found, id: " + eventId)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(objectMapper, res, problem);
            return;
        }

        val eventDetails = eventDetailsM.orElseThrow();

        val commonWeb3Details = Web3CommonDetails.builder()
                .event(eventDetails)
                .walletType(CARDANO)
                .walletId(walletId)
                .chainTip(chainTip)
                .action(web3Action)
                .network(chainNetwork)
                .build();

        val cardanoWeb3Details = CardanoWeb3Details.builder()
                .web3CommonDetails(commonWeb3Details)
                .envelope(genericEnvelope)
                .signedCIP30(signedWeb3Request)
                .cip30VerificationResult(cipVerificationResult)
                .build();

        val authentication = new Web3AuthenticationToken(cardanoWeb3Details, List.of(new SimpleGrantedAuthority(VOTER.name())));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

}
