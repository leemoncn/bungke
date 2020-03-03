package com.leemon.wushiwan.system.mapper;

import com.leemon.wushiwan.system.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 角色-菜单 Mapper 接口
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
	List<SysRoleMenu> selectSysRoleMenuByRoleName(String roleName);

	/**
	 * 根据roleId删除全部的记录，真实删除
	 *
	 * @param roleId
	 */
	void deleteAllByRoleId(Integer roleId);
}
