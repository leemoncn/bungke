package com.leemon.wushiwan.jwt;

import com.leemon.wushiwan.system.entity.SysUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-18 11:06
 **/
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private SysUser principal;
	//jwt加密后的token
	private String credentials;

	public JwtAuthenticationToken(String credentials) {
		super(null);
		this.credentials = credentials;
	}

	public JwtAuthenticationToken(SysUser principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
	}

	/**
	 * The credentials that prove the principal is correct. This is usually a password,
	 * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
	 * are expected to populate the credentials.
	 *
	 * @return the credentials that prove the identity of the <code>Principal</code>
	 */
	@Override
	public Object getCredentials() {
		return credentials;
	}

	/**
	 * The identity of the principal being authenticated. In the case of an authentication
	 * request with username and password, this would be the username. Callers are
	 * expected to populate the principal for an authentication request.
	 * <p>
	 * The <tt>AuthenticationManager</tt> implementation will often return an
	 * <tt>Authentication</tt> containing richer information as the principal for use by
	 * the application. Many of the authentication providers will create a
	 * {@code UserDetails} object as the principal.
	 *
	 * @return the <code>Principal</code> being authenticated or the authenticated
	 * principal after authentication.
	 */
	@Override
	public Object getPrincipal() {
		return principal;
	}
}
