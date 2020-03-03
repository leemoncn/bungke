package com.leemon.wushiwan.jwt;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.system.entity.SysRole;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.enums.LoginStatus;
import com.leemon.wushiwan.enums.UserType;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.service.ISysRoleMenuService;
import com.leemon.wushiwan.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: limeng
 * @create: 2019-04-03 19:52
 **/
@Component
public class GALogic {

	private final ISysRoleService sysRoleService;
	private final ISysRoleMenuService sysRoleMenuService;

	@Autowired
	public GALogic(ISysRoleService sysRoleService, ISysRoleMenuService sysRoleMenuService) {
		this.sysRoleService = sysRoleService;
		this.sysRoleMenuService = sysRoleMenuService;
	}

	public List<RolePermissionGrantedAuthority> createGAList(SysUser sysUser) {
		if (sysUser.getLoginStatusPropertyId() == LoginStatus.DENIED) {
			throw new LogicException("登陆失败," + sysUser.getLoginStatusPropertyId().getName());
		}
		List<RolePermissionGrantedAuthority> list = new ArrayList<>();
		if (sysUser.getUserType() == UserType.CLIENT) {//前端用户，无需查询权限
			return list;
		}
		//先查出拥有哪些角色
		List<SysRole> roleList = sysRoleService.getRolesByUserId(sysUser.getId());
		if (roleList.size() == 0) {
			throw new LogicException(ErrorCode.ROLE_DISABLED);
		}
		roleList.forEach(sysRole -> {//查出这些角色拥有哪些权限
			List<String> permissionList = sysRoleMenuService.getMenuPermissionsByRoleId(sysRole.getId());
			RolePermissionGrantedAuthority ga = new RolePermissionGrantedAuthority(sysRole.getRoleName(), permissionList);
			list.add(ga);
		});
		return list;
	}
}
