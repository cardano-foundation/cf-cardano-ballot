package org.cardano.foundation.voting.service.auth.web3;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.cardano.foundation.voting.utils.Enums;
import org.cardano.foundation.voting.utils.StakeAddress;
import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.Role.VOTER;
import static org.cardano.foundation.voting.resource.Headers.XCIP93PublicKey;
import static org.cardano.foundation.voting.resource.Headers.XCIP93Signature;
import static org.cardano.foundation.voting.service.auth.LoginSystem.CIP93;
import static org.cardano.foundation.voting.utils.MoreNumber.isNumeric;
import static org.cardanofoundation.cip30.MessageFormat.TEXT;
import static org.zalando.problem.Status.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Web3Filter extends OncePerRequestFilter {

    private final JsonService jsonService;

    private final ExpirationService expirationService;

    private final ObjectMapper objectMapper;

    // TODO can we do this via local access?
    private final ChainFollowerClient chainFollowerClient;

    private final CardanoNetwork cardanoNetwork;

    private final LoginSystemDetector loginSystemDetector;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var maybeLoginSystem = loginSystemDetector.detect(request);

        if (maybeLoginSystem.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        var loginSystem = maybeLoginSystem.orElseThrow();

        if (loginSystem != CIP93) {
            chain.doFilter(request, response);
            return;
        }

        var coseSignature = request.getHeader(XCIP93Signature);
        var cosePublicKey = request.getHeader(XCIP93PublicKey);

        if (coseSignature == null) {
            var problem = Problem.builder()
                    .withTitle("NO_CIP93_HTTP_HEADERS_SET")
                    .withDetail("CIP-93 http headers must be set.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var signedWeb3Request = new SignedWeb3Request(coseSignature, Optional.ofNullable(cosePublicKey));

        var cip30Verifier = new CIP30Verifier(signedWeb3Request.getCoseSignature(), signedWeb3Request.getCosePublicKey());
        var cipVerificationResult = cip30Verifier.verify();

        if (!cipVerificationResult.isValid()) {
            log.info("Unable to decode valid signed web3 request!");

            var problem = Problem.builder()
                    .withTitle("INVALID_CIP30_DATA_SIGNATURE")
                    .withDetail("Invalid CIP93 cose signature!")
                    .withStatus(UNAUTHORIZED)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var maybeAddress = cipVerificationResult.getAddress(AddressFormat.TEXT);
        if (maybeAddress.isEmpty()) {
            var problem = Problem.builder()
                    .withTitle("ADDRESS_NOT_FOUND")
                    .withDetail("Bech32 address not found in the signed data.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }
        var stakeAddress = maybeAddress.orElseThrow();

        var cipBody = cipVerificationResult.getMessage(TEXT);

        var cip93EnvelopeE = jsonService.decodeGenericCIP93(cipBody);
        if (cip93EnvelopeE.isEmpty()) {
            log.info("Invalid CIP-93 envelope!");

            var problem = Problem.builder()
                    .withTitle("INVALID_CIP93_ENVELOPE")
                    .withDetail("CIP-93 envelope decoding failed.")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var genericEnvelope = cip93EnvelopeE.get();

        var maybeWeb3Action = genericEnvelope.getActionAsEnum();
        if (maybeWeb3Action.isEmpty()) {
            log.info("Unrecognised action, action:{}", genericEnvelope.getAction());

            var problem = Problem.builder()
                    .withTitle("ACTION_NOT_FOUND")
                    .withDetail("Action not found!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var web3Action = maybeWeb3Action.orElseThrow();

        String slotStr = genericEnvelope.getSlot();
        if (!isNumeric(slotStr)) {
            var problem = Problem.builder()
                            .withTitle("INVALID_SLOT")
                            .withDetail("CIP-93 envelope slot is not numeric!")
                            .withStatus(BAD_REQUEST)
                            .build();

            sendBackProblem(response, problem);
            return;
        }

        var slot = genericEnvelope.getSlotAsLong();
        var slotExpiredE = expirationService.isSlotExpired(slot);
        if (slotExpiredE.isEmpty()) {
            logger.warn("Unable to lookup slot data!");

            var problem = Problem.builder()
                    .withTitle("SLOT_LOOKUP_FAILURE")
                    .withDetail("CIP-93 lookup failure!")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build();

            sendBackProblem(response, problem);
            return;
        }
        var slotExpired = slotExpiredE.get();

        if (slotExpired) {
            var problem = Problem.builder()
                    .withTitle("EXPIRED_SLOT")
                    .withDetail("CIP-93 envelope slot expired!")
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var eventId = (String) genericEnvelope.getData().get("event");
        var networkString = (String) genericEnvelope.getData().get("network");
        var envelopeStakeAddress = genericEnvelope.getData().get("address");

        var maybeNetwork = Enums.getIfPresent(CardanoNetwork.class, networkString);
        if (maybeNetwork.isEmpty()) {
            var problem = Problem.builder()
                    .withTitle("INVALID_NETWORK")
                    .withDetail("Invalid network, supported networks:" + CardanoNetwork.supportedNetworks())
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }
        var network = maybeNetwork.get();

        if (network != cardanoNetwork) {
            log.warn("Invalid network, network:{}", networkString);

            var problem = Problem.builder()
                    .withTitle("NETWORK_MISMATCH")
                    .withDetail("Invalid network, backed configured with network:" + cardanoNetwork + ", however request is with network:" + network)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var stakeAddressCheckE = StakeAddress.checkStakeAddress(network, stakeAddress);
        if (stakeAddressCheckE.isEmpty()) {
            var problem = stakeAddressCheckE.getLeft();

            sendBackProblem(response, problem);
            return;
        }

        if (!stakeAddress.equals(envelopeStakeAddress)) {
            var problem = Problem.builder()
                    .withTitle("STAKE_ADDRESS_MISMATCH")
                    .withDetail("Stake address mismatch, CIP-93 signed address:" + stakeAddress + ", however request is with address:" + envelopeStakeAddress)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var eventDetailsE = chainFollowerClient.getEventDetails(eventId);
        if (eventDetailsE.isEmpty()) {
            var problem = Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + eventId)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var maybeEventDetails = eventDetailsE.get();
        if (maybeEventDetails.isEmpty()) {
            var problem = Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build();

            sendBackProblem(response, problem);
            return;
        }

        var eventDetails = maybeEventDetails.orElseThrow();

        var web3Details = Web3Details.builder()
                .event(eventDetails)
                .stakeAddress(stakeAddress)
                .signedWeb3Request(signedWeb3Request)
                .action(web3Action)
                .envelope(genericEnvelope)
                .cip30VerificationResult(cipVerificationResult)
                .network(network)
                .build();

        var authentication = new Web3AuthenticationToken(web3Details, List.of(new SimpleGrantedAuthority(VOTER.name())));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private void sendBackProblem(HttpServletResponse response, Problem problem) throws IOException {
        var statusCode = problem.getStatus().getStatusCode();

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().println(objectMapper.writeValueAsString(problem));
        response.getOutputStream().flush();
    }

}
