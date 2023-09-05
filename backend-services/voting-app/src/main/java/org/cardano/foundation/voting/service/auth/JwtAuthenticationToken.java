package org.cardano.foundation.voting.service.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nullable;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {


    private final JwtPrincipal principal;

    public JwtAuthenticationToken(JwtPrincipal principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.
                setAuthenticated(true);
    }

    @Override
    public Object getDetails() {
        return principal.getSignedJWT();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    @Nullable
    public Object getCredentials() {
        return null;
    }

}