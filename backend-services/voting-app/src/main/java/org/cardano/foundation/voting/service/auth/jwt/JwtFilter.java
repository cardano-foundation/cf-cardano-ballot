package org.cardano.foundation.voting.service.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.auth.LoginSystemDetector;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

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
            var role = signedJwt.getJWTClaimsSet().getStringClaim("role");

            var authentication = new JwtAuthenticationToken(new JwtPrincipal(signedJwt), List.of(new SimpleGrantedAuthority(role)));
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
