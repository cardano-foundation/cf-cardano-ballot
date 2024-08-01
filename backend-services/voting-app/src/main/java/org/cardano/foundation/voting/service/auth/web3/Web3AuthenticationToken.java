package org.cardano.foundation.voting.service.auth.web3;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nullable;
import java.util.Collection;

public class Web3AuthenticationToken extends AbstractAuthenticationToken {

    private final Web3ConcreteDetails web3ConcreteDetails;

    public Web3AuthenticationToken(Web3ConcreteDetails web3ConcreteDetails,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.web3ConcreteDetails = web3ConcreteDetails;
        this.setAuthenticated(true);
    }

    @Override
    public Web3ConcreteDetails getDetails() {
        return web3ConcreteDetails;
    }

    @Override
    public Object getPrincipal() {
        return web3ConcreteDetails.getWeb3CommonDetails().getWalletId();
    }

    @Override
    @Nullable
    public Object getCredentials() {
        return null;
    }

}
