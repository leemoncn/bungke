package com.leemon.wushiwan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.entity.SysMenu;
import com.leemon.wushiwan.system.entity.SysRole;
import com.leemon.wushiwan.system.mapper.SysMenuMapper;
import com.leemon.wushiwan.system.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.service.ISysRoleMenuService;
import com.leemon.wushiwan.system.service.ISysRoleService;
import com.leemon.wushiwan.system.vo.TreeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

	private final SysMenuMapper sysMenuMapper;
	private final ISysRoleMenuService sysRoleMenuService;
	private final ISysRoleService sysRoleService;

	@Autowired
	public SysMenuServiceImpl(SysMenuMapper sysMenuMapper, ISysRoleMenuService sysRoleMenuService, ISysRoleService sysRoleService) {
		this.sysMenuMapper = sysMenuMapper;
		this.sysRoleMenuService = sysRoleMenuService;
		this.sysRoleService = sysRoleService;
	}

	@Override
	public void deletePermissionLogical(Integer id) {
		SysMenu sysPermission = this.getById(id);
		if (sysPermission == null) {
			throw new LogicException("未找到菜单信息");
		}
		this.removeById(id);
	}

	@Override
	public List<TreeModel> queryListByParentId(Integer parentId) {
		return sysMenuMapper.queryListByParentId(parentId);
	}

	@Override
	public void addSysMenu(SysMenu sysMenu) {
		this.save(sysMenu);
	}

	@Override
	public void editSysMenu(SysMenu sysMenu) {
		SysMenu p = this.getById(sysMenu.getId());
		if (p == null) {
			throw new LogicException("未找到菜单信息");
		}
		this.updateById(sysMenu);
//		//TODO 该节点判断是否还有子节点
	}

	@Override
	public List<SysMenu> queryByUserId(Integer userId) {
		List<SysRole> roleList = sysRoleService.getRolesByUserId(userId);
		if (roleList.stream().anyMatch(sysRole -> "admin".equals(sysRole.getRoleName()) && sysRole.getUseable())) {//管理员拥有全部的菜单权限
			LambdaQueryWrapper<SysMenu> query = new LambdaQueryWrapper<>();
			query.orderByAsc(SysMenu::getSort);
			return sysMenuMapper.selectList(query);
		}
		return sysMenuMapper.queryByUserId(userId);
	}
}
