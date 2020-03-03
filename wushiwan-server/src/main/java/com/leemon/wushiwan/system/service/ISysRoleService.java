package com.leemon.wushiwan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
public interface ISysRoleService extends IService<SysRole> {
	List<SysRole> getRolesByUserId(Integer userId);
	Integer getRoleIdByRoleName(String roleName);
}
