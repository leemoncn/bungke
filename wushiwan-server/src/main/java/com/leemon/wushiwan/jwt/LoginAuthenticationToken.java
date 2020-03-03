package com.leemon.wushiwan.jwt;

import com.leemon.wushiwan.entity.CoreLoginAuth;
import com.leemon.wushiwan.system.entity.SysUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @description:
 * @author: limeng
 * @create: 2019-03-24 13:09
 **/
public class LoginAuthenticationToken extends AbstractAuthenticationToken {

	private SysUser principal;
	//	CoreLoginAuth
	private CoreLoginAuth credentials;

	public LoginAuthenticationToken(CoreLoginAuth credentials) {
		super(null);
		this.credentials = credentials;
	}

	public LoginAuthenticationToken(SysUser principal, CoreLoginAuth credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
