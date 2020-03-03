package com.leemon.wushiwan.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * @description:
 * @author: leemon
 * @create: 2019-04-03 16:06
 **/
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		CustomMethodSecurityExpressionHandler expressionHandler =
				new CustomMethodSecurityExpressionHandler();
//		expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
		return expressionHandler;
	}
}
