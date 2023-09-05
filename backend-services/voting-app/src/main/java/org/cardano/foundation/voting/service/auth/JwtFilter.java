package org.cardano.foundation.voting.service.auth;

import com.google.common.net.HttpHeaders;
import com.nimbusds.jwt.SignedJWT;
import io.vavr.control.Either;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.text.ParseException;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Service
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and verificationResultE
        var token = header.split(" ")[1].trim();
        Either<Problem, SignedJWT> verificationResultE = jwtService.verify(token);

        if (verificationResultE.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        var signedJwt = verificationResultE.get();

        try {
            var role = signedJwt.getJWTClaimsSet().getStringClaim("role");

            var authentication = new JwtAuthenticationToken(new JwtPrincipal(signedJwt), List.of(new SimpleGrantedAuthority(role)));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user identity and set it on the spring security context
            chain.doFilter(request, response);
        } catch (ParseException e) {
            log.error("JWT parse exception", e);
            throw new IOException("JWT parse exception", e);
        }
    }

}