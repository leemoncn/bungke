package com.leemon.wushiwan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.leemon.wushiwan.system.entity.SysRoleMenu;
import com.leemon.wushiwan.system.mapper.SysMenuMapper;
import com.leemon.wushiwan.system.mapper.SysRoleMenuMapper;
import com.leemon.wushiwan.system.service.ISysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色-菜单 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

	private final SysMenuMapper sysMenuMapper;
	private final SysRoleMenuMapper sysRoleMenuMapper;

	@Autowired
	public SysRoleMenuServiceImpl(SysMenuMapper sysMenuMapper, SysRoleMenuMapper sysRoleMenuMapper) {
		this.sysMenuMapper = sysMenuMapper;
		this.sysRoleMenuMapper = sysRoleMenuMapper;
	}

	@Cacheable(value = "SysRoleMenu")
	@Override
	public List<String> getMenuPermissionsByRoleId(Integer roleId) {
		QueryWrapper<SysRoleMenu> qw = new QueryWrapper<>();
		qw.lambda().eq(SysRoleMenu::getRoleId, roleId);
		List<SysRoleMenu> list = list(qw);
		List<Integer> menuIdList = list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
		List<String> permissionList = new ArrayList<>();
		if (menuIdList.size() == 0) {
			return permissionList;
		}
		//添加增删改权限
		list.forEach(sysRoleMenu -> {
			String addPermission = sysRoleMenu.getAddPermission();
			if (!Strings.isNullOrEmpty(addPermission) && !permissionList.contains(addPermission)) {
				permissionList.add(addPermission);
			}
			String editPermission = sysRoleMenu.getEditPermission();
			if (!Strings.isNullOrEmpty(editPermission) && !permissionList.contains(editPermission)) {
				permissionList.add(editPermission);
			}
			String deletePermission = sysRoleMenu.getDeletePermission();
			if (!Strings.isNullOrEmpty(deletePermission) && !permissionList.contains(deletePermission)) {
				permissionList.add(deletePermission);
			}
		});
		//获取查看权限
		List<String> viewPermissionList = sysMenuMapper.selectMenuPermissionsByMenuIdList(menuIdList);
		viewPermissionList.forEach(s -> {
			if (!permissionList.contains(s)) {
				permissionList.add(s);
			}
		});
		return permissionList;
	}

	/**
	 * 保存授权/先删后增
	 *
	 * @param roleId
	 * @param menuIds
	 */
	@Caching(evict = {
			@CacheEvict(value = "SysRoleMenu", allEntries = true)
	})
	@Override
	public void saveRoleMenu(Integer roleId, String menuIds) {
		LambdaQueryWrapper<SysRoleMenu> query = new QueryWrapper<SysRoleMenu>().lambda().eq(SysRoleMenu::getRoleId, roleId);
		this.remove(query);
		List<SysRoleMenu> list = new ArrayList<>();
		String[] arr = menuIds.split(",");
		for (String p : arr) {
			if (ConvertUtils.isNotEmpty(p)) {
				SysRoleMenu sysRoleMenu = new SysRoleMenu();
				sysRoleMenu.setRoleId(roleId);
				sysRoleMenu.setMenuId(Integer.parseInt(p));
				list.add(sysRoleMenu);
			}
		}
		this.saveBatch(list);
	}

	@Cacheable(value = "SysRoleMenu")
	@Override
	public List<SysRoleMenu> querySysRoleMenuByRoleName(String roleName) {
		return sysRoleMenuMapper.selectSysRoleMenuByRoleName(roleName);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRoleMenu", allEntries = true)
	})
	@Override
	public boolean saveOrUpdate(SysRoleMenu entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRoleMenu", allEntries = true)
	})
	@Override
	public boolean updateById(SysRoleMenu entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRoleMenu", allEntries = true)
	})
	@Override
	public boolean update(SysRoleMenu entity, Wrapper<SysRoleMenu> updateWrapper) {
		return super.update(entity, updateWrapper);
	}

	@Cacheable(value = "SysRoleMenu", key = "#id")
	@Override
	public SysRoleMenu getById(Serializable id) {
		return super.getById(id);
	}

	/**
	 * 给某个角色添加某个菜单的所有增删改查权限
	 *
	 * @param roleId
	 * @param menuId
	 * @param permissionTag
	 */
	@Override
	public void addAllPermissionForRole(Integer roleId, Integer menuId, String permissionTag) {
		assert false;
//		SysRoleMenu srm = new SysRoleMenu();
//		srm.setMenuId(menuId);
//		srm.setRoleId(roleId);
//		srm.setPermission(permissionTag);
//		this.save(srm);
//		if (!Strings.isNullOrEmpty(permissionTag)) {
//			srm.setPermission(permissionTag + "_add").setId(null);
//			this.save(srm);
//			srm.setPermission(permissionTag + "_edit").setId(null);
//			this.save(srm);
//			srm.setPermission(permissionTag + "_delete").setId(null);
//			this.save(srm);
//		}
	}
}
