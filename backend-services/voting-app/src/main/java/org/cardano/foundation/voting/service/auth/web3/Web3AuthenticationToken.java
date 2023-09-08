package org.cardano.foundation.voting.service.auth.web3;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nullable;
import java.util.Collection;

public class Web3AuthenticationToken extends AbstractAuthenticationToken {

    private final Web3Details web3Details;

    public Web3AuthenticationToken(Web3Details web3Details,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.web3Details = web3Details;
        this.setAuthenticated(true);
    }

    @Override
    public Web3Details getDetails() {
        return web3Details;
    }

    @Override
    public Object getPrincipal() {
        return web3Details.getStakeAddress();
    }

    @Override
    @Nullable
    public Object getCredentials() {
        return null;
    }

}
