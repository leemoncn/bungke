package com.leemon.wushiwan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.system.entity.SysUserRole;
import com.leemon.wushiwan.system.mapper.SysUserRoleMapper;
import com.leemon.wushiwan.system.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户-角色 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

	@Override
	public List<SysUserRole> getSysUserRoleListByUserId(int userId) {
		QueryWrapper<SysUserRole> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUserRole::getUserId, userId);
		return list(qw);
	}
}
