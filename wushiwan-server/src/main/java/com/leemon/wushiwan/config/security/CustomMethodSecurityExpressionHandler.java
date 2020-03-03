package com.leemon.wushiwan.config.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * @description:
 * @author: leemon
 * @create: 2019-04-03 16:05
 **/
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
			Authentication authentication, MethodInvocation invocation) {
		CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(this.trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}
}
