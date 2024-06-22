package edu.kcg.futurelab.madoi.volatileserver.config.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthTokenAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1022073316972351441L;

	public AuthTokenAuthentication(String authToken, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.authToken = authToken;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authToken;
	}

	private String authToken;
}