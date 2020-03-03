package com.leemon.wushiwan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.entity.SysRole;
import com.leemon.wushiwan.system.mapper.SysRoleMapper;
import com.leemon.wushiwan.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	private final SysRoleMapper sysRoleMapper;

	@Autowired
	public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
		this.sysRoleMapper = sysRoleMapper;
	}

	@Cacheable(value = "SysRole")
	@Override
	public List<SysRole> getRolesByUserId(Integer userId) {
		return sysRoleMapper.selectRolesByUserId(userId);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRole", allEntries = true)
	})
	@Override
	public boolean saveOrUpdate(SysRole entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRole", allEntries = true)
	})
	@Override
	public boolean updateById(SysRole entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysRole", allEntries = true)
	})
	@Override
	public boolean update(SysRole entity, Wrapper<SysRole> updateWrapper) {
		return super.update(entity, updateWrapper);
	}

	@Cacheable(value = "SysRole", key = "#id")
	@Override
	public SysRole getById(Serializable id) {
		return super.getById(id);
	}

	@Override
	public Integer getRoleIdByRoleName(String roleName) {
		LambdaQueryWrapper<SysRole> lqw = new LambdaQueryWrapper<>();
		lqw.eq(SysRole::getRoleName, roleName);
		SysRole sysRole = this.getOne(lqw);
		if (sysRole == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "未查到roleName = %s", roleName);
		}
		return sysRole.getId();
	}

}
