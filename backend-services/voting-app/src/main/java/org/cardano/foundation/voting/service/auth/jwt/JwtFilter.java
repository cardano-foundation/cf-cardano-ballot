package org.cardano.foundation.voting.service.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Role;
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
        var maybeLoginSystem = loginSystemDetector.detect(request);

        if (maybeLoginSystem.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        var loginSystem = maybeLoginSystem.orElseThrow();

        if (loginSystem != JWT) {
            chain.doFilter(request, response);
            return;
        }

        var header = request.getHeader(AUTHORIZATION);
        var token = header.split(" ")[1].trim();
        var verificationResultE = jwtService.verify(token);

        if (verificationResultE.isEmpty()) {
            sendBackProblem(response, verificationResultE.getLeft());
            return;
        }

        var signedJwt = verificationResultE.get();

        try {
            var jwtClaimsSet = signedJwt.getJWTClaimsSet();
            var eventId = jwtClaimsSet.getStringClaim("eventId");
            var stakeAddress = jwtClaimsSet.getStringClaim("stakeAddress");
            var role = Enums.getIfPresent(Role.class, jwtClaimsSet.getStringClaim("role")).orElseThrow();
            var principal = new JwtPrincipal(signedJwt);
            var authorities = List.of(new SimpleGrantedAuthority(role.name()));

            var eventDetailsE = chainFollowerClient.getEventDetails(eventId);
            if (eventDetailsE.isEmpty()) {
                var problem = Problem.builder()
                        .withTitle("ERROR_GETTING_EVENT_DETAILS")
                        .withDetail("Unable to get eventDetails details from chain-tip follower service, eventDetails:" + eventId)
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build();

                sendBackProblem(response, problem);
                return;
            }

            var maybeEventDetails = eventDetailsE.get();
            if (maybeEventDetails.isEmpty()) {
                var problem = Problem.builder()
                        .withTitle("EVENT_NOT_FOUND")
                        .withDetail("Event not found, id:" + eventId)
                        .withStatus(BAD_REQUEST)
                        .build();

                sendBackProblem(response, problem);
                return;
            }
            var eventDetails = maybeEventDetails.orElseThrow();

            var authentication = new JwtAuthenticationToken(principal, authorities)
                    .eventDetails(eventDetails)
                    .role(role)
                    .stakeAddress(stakeAddress)
                    ;

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (ParseException e) {
            log.warn("JWT parse error, reason:{}", e.getMessage());

            var problem = Problem.builder()
                            .withTitle("JWT_PARSE_ERROR")
                            .withStatus(BAD_REQUEST)
                            .build();

            sendBackProblem(response, problem);
        }
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
