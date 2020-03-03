package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.entity.SysProperty;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.enums.base.PropertyType;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.SysPropertyMapper;
import com.leemon.wushiwan.service.ISysPropertyService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.util.SpringUtil;
import com.leemon.wushiwan.vo.SysUserDetail;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class SysPropertyServiceImpl extends ServiceImpl<SysPropertyMapper, SysProperty> implements ISysPropertyService {

	@Caching(evict = {
			@CacheEvict(value = "SysProperty", key = "#entity.id", condition = "#entity.id != null")
	})
	@Override
	public boolean saveOrUpdate(SysProperty entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysProperty", key = "#entity.id")
	})
	@Override
	public boolean updateById(SysProperty entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "SysProperty", allEntries = true)
	})
	@Override
	public boolean update(SysProperty entity, Wrapper<SysProperty> updateWrapper) {
		return super.update(entity, updateWrapper);
	}

	@Cacheable(value = "SysProperty", key = "#id")
	@Override
	public SysProperty getById(Serializable id) {
		return super.getById(id);
	}

	@Override
	public PropertyType getPropertyTypeById(Integer id) {
		ISysPropertyService service = SpringUtil.getBean(ISysPropertyService.class);
		SysProperty sp = service.getById(id);
		if (sp == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "获取PropertyType,id = %d为空", id);
		}
		if (sp.getType() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "获取PropertyType,id = %d,type为空", id);
		}
		return PropertyType.values()[sp.getType()];
	}
}

