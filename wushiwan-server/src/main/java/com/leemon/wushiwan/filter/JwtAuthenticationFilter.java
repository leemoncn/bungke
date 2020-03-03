package com.leemon.wushiwan.filter;

import com.leemon.wushiwan.config.JwtProperties;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.jwt.JwtAuthenticationToken;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: JWT的登录过滤器
 * @author: leemon
 * @create: 2019-03-18 09:56
 **/
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtProperties jwtProperties;
	private AuthenticationManager authenticationManager;
	private JwtTokenUtil jwtTokenUtil;

	public JwtAuthenticationFilter(JwtProperties jwtProperties, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
		this.jwtProperties = jwtProperties;
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	/**
	 * Same contract as for {@code doFilter}, but guaranteed to be
	 * just invoked once per request within a single request thread.
	 * See {@link #shouldNotFilterAsyncDispatch()} for details.
	 * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
	 * default ServletRequest and ServletResponse ones.
	 *
	 * @param request
	 * @param response
	 * @param filterChain
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = request.getHeader(jwtProperties.getHeader());
		if (!jwtTokenUtil.isTokenInWhiteList(token)) {
			throw new LogicException(ErrorCode.LOGIN_FAILED);
		}
		if (token != null) {
			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token);
				Authentication authResult = authenticationManager.authenticate(authRequest);
				SecurityContextHolder.getContext().setAuthentication(authResult);
			}
		}
		filterChain.doFilter(request, response);
	}
}
