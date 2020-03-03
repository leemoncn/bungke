package com.leemon.wushiwan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.entity.SysUserRole;

import java.util.List;

/**
 * <p>
 * 用户-角色 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

	List<SysUserRole> getSysUserRoleListByUserId(int userId);
}
