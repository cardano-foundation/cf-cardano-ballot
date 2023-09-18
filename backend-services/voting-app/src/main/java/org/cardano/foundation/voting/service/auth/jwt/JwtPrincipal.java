package org.cardano.foundation.voting.service.auth.jwt;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.security.Principal;

public class JwtPrincipal implements Principal, AuthenticatedPrincipal {

    private final SignedJWT signedJWT;

    public JwtPrincipal(SignedJWT signedJWT) {
        this.signedJWT = signedJWT;
    }

    public SignedJWT getSignedJWT() {
        return signedJWT;
    }

    @Override
    @SneakyThrows
    public String getName() {
        return signedJWT.getJWTClaimsSet().getSubject();
    }

}
