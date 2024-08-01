package org.cardano.foundation.voting.service.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
import org.cardano.foundation.voting.utils.Enums;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.cardano.foundation.voting.service.auth.LoginSystem.JWT;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final ChainFollowerClient chainFollowerClient;
    private final JwtService jwtService;
    private final LoginSystemDetector loginSystemDetector;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        val maybeLoginSystem = loginSystemDetector.detect(request);

        if (maybeLoginSystem.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        val loginSystem = maybeLoginSystem.orElseThrow();

        if (loginSystem != JWT) {
            chain.doFilter(request, response);
            return;
        }

        val header = request.getHeader(AUTHORIZATION);
        val token = header.split(" ")[1].trim();
        val verificationResultE = jwtService.verify(token);

        if (verificationResultE.isEmpty()) {
            sendBackProblem(response, verificationResultE.getLeft());
            return;
        }

        val signedJwt = verificationResultE.get();

        try {
            val jwtClaimsSet = signedJwt.getJWTClaimsSet();
            val eventId = jwtClaimsSet.getStringClaim("eventId");
            val walletTypeM = Enums.getIfPresent(WalletType.class, jwtClaimsSet.getStringClaim("walletType"));

            if (walletTypeM.isEmpty()) {
                val problem = Problem.builder()
                        .withTitle("INVALID_WALLET_TYPE")
                        .withDetail("Invalid wallet type in JWT claims, walletType:" + jwtClaimsSet.getStringClaim("walletType"))
                        .withStatus(BAD_REQUEST)
                        .build();

                sendBackProblem(response, problem);
                return;
            }

            val walletType = walletTypeM.orElseThrow();
            val walletId = jwtClaimsSet.getStringClaim("walletId");
            
            val role = Enums.getIfPresent(Role.class, jwtClaimsSet.getStringClaim("role")).orElseThrow();
            val principal = new JwtPrincipal(signedJwt);
            val authorities = List.of(new SimpleGrantedAuthority(role.name()));

            val eventDetailsE = chainFollowerClient.getEventDetails(eventId);
            if (eventDetailsE.isEmpty()) {
                val problem = Problem.builder()
                        .withTitle("ERROR_GETTING_EVENT_DETAILS")
                        .withDetail("Unable to get eventDetails details from chain-tip follower service, eventDetails:" + eventId)
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build();

                sendBackProblem(response, problem);
                return;
            }

            val maybeEventDetails = eventDetailsE.get();
            if (maybeEventDetails.isEmpty()) {
                val problem = Problem.builder()
                        .withTitle("UNRECOGNISED_EVENT")
                        .withDetail("Event not found, id: " + eventId)
                        .withStatus(BAD_REQUEST)
                        .build();

                sendBackProblem(response, problem);
                return;
            }
            val eventDetails = maybeEventDetails.orElseThrow();

            val authentication = new JwtAuthenticationToken(principal, authorities)
                    .eventDetails(eventDetails)
                    .role(role)
                    .walletId(walletId)
                    .walletType(walletType);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (ParseException e) {
            log.warn("JWT parse error, reason:{}", e.getMessage());

            val problem = Problem.builder()
                            .withTitle("JWT_PARSE_ERROR")
                            .withStatus(BAD_REQUEST)
                            .build();

            sendBackProblem(response, problem);
        }
    }

    private void sendBackProblem(HttpServletResponse response, Problem problem) throws IOException {
        val statusCode = problem.getStatus().getStatusCode();

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().println(objectMapper.writeValueAsString(problem));
        response.getOutputStream().flush();
    }

}
