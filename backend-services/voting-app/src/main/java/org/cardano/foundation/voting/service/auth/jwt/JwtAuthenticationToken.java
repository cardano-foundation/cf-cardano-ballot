package org.cardano.foundation.voting.service.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Role;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nullable;
import java.util.Collection;

@Setter
@Getter
@Accessors(fluent = true)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtPrincipal principal;

    private ChainFollowerClient.EventDetailsResponse eventDetails;

    private String stakeAddress;

    private Role role;

    public JwtAuthenticationToken(JwtPrincipal principal,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.setAuthenticated(true);
    }

    public boolean isActionAllowed(Web3Action action) {
        var allowedRoles = role.allowedActions();

        return allowedRoles.contains(action);
    }

    public boolean isActionNotAllowed(Web3Action action) {
        return !isActionAllowed(action);
    }

    @SneakyThrows
    public String getStakeAddress() {
        return stakeAddress;
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
