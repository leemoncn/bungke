package com.leemon.wushiwan.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * @description:
 * @author: leemon
 * @create: 2019-04-03 16:38
 **/
@Slf4j
public class RolePermissionGrantedAuthority implements GrantedAuthority {

	private String role;
	private List<String> permissionList;

	public RolePermissionGrantedAuthority(String role, List<String> permissionList) {
		this.role = role;
		this.permissionList = permissionList;
	}

	@Override
	public String getAuthority() {
		return role;
	}

	public boolean containsPermission(String permission) {
		return permissionList.contains(permission);
	}
}
