package com.leemon.wushiwan.config.security;

import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.jwt.RolePermissionGrantedAuthority;
import com.leemon.wushiwan.system.entity.SysUser;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @description:
 * @author: leemon
 * @create: 2019-04-03 15:47
 **/
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private Object filterObject;
	private Object returnObject;
	private Object target;
	private Authentication authentication;

	public CustomMethodSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
		this.authentication = authentication;
	}

	public boolean isMember(String permission) {
		return false;
	}

	public boolean hasPermission(String permission) {
		Collection<? extends GrantedAuthority> c = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : c) {
			RolePermissionGrantedAuthority ga = (RolePermissionGrantedAuthority) grantedAuthority;
			if ("admin".equals(ga.getAuthority())) {
				return true;
			}
			if (ga.containsPermission(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是前端用户,在BaseController中加了这个，所有子类，如果是后台接口，自己覆盖@PreAuthorize即可
	 *
	 * @return
	 */
	public boolean isClientUser() {
		if (authentication == null || authentication.getPrincipal() == null) {
			return false;
		}
		SysUser sysUser = (SysUser) authentication.getPrincipal();
		return sysUser.getUserType() == UserType.CLIENT;
	}

	/**
	 * 所有用户均允许
	 *
	 * @return
	 */
	public boolean allPermitted() {
		return true;
	}

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {
		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {
		return returnObject;
	}

	@Override
	public Object getThis() {
		return target;
	}

	public void setThis(Object target) {
		this.target = target;
	}

}
