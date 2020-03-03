package com.leemon.wushiwan.system.service;

import com.leemon.wushiwan.system.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色-菜单 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {
	List<String> getMenuPermissionsByRoleId(Integer roleId);

	/**
	 * 保存授权/先删后增
	 *
	 * @param roleId
	 * @param menuIds
	 */
	void saveRoleMenu(Integer roleId, String menuIds);

	List<SysRoleMenu> querySysRoleMenuByRoleName(String roleName);

	/**
	 * 给某个角色添加某个菜单的所有增删改查权限
	 *
	 * @param roleId
	 * @param menuId
	 * @param permissionTag
	 */
	void addAllPermissionForRole(Integer roleId, Integer menuId, String permissionTag);
}
