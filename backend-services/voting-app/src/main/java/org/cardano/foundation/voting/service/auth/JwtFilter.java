package org.cardano.foundation.voting.service.auth;

import com.nimbusds.jwt.SignedJWT;
import io.vavr.control.Either;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static io.micrometer.common.util.StringUtils.isEmpty;

@Service
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var header = request.getHeader(AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and verificationResult
        var token = header.split(" ")[1].trim();
        Either<Problem, SignedJWT> verificationResultE = jwtService.verify(token);

        if (verificationResultE.isEmpty()) {
            log.warn("JWT verification failed: {}", verificationResultE.getLeft());

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
