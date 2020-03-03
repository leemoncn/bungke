package com.leemon.wushiwan.jwt;

import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-18 10:58
 **/
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private GALogic gaLogic;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication au = SecurityContextHolder.getContext().getAuthentication();
		if (au != null) {
			return au;
		}
		JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
		String token = (String) jwtToken.getCredentials();
		String userIdStr = jwtTokenUtil.getUserIdFromToken(token);
		if (userIdStr != null) {
			//token中存储的userid
			Integer userId = Integer.parseInt(userIdStr);
			SysUser sysUser = sysUserService.getById(userId);
			if (jwtTokenUtil.validateToken(token)) {//token是否过期
				List<RolePermissionGrantedAuthority> list = gaLogic.createGAList(sysUser);
				return new JwtAuthenticationToken(sysUser, token, list);
			} else {// jwt过期
				throw new LogicException(ErrorCode.LOGIN_FAILED);
			}
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(JwtAuthenticationToken.class);
	}
}
