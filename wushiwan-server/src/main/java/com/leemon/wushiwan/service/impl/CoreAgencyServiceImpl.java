package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.leemon.wushiwan.entity.CoreAgency;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreAgencyMapper;
import com.leemon.wushiwan.service.ICoreAgencyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class CoreAgencyServiceImpl extends ServiceImpl<CoreAgencyMapper, CoreAgency> implements ICoreAgencyService {

	@Cacheable(value = "CoreAgency", key = "'ExistCoreAgency'")
	@Override
	public CoreAgency getExistCoreAgency() {
		List<CoreAgency> list = list();
		if (list.size() == 0) {
			throw new LogicException(ErrorCode.SYS_ERROR, "请先配置一条抽成配置");
		}
		return list.get(0);
	}

	@Cacheable(value = "CoreAgency")
	@Override
	public CoreAgency getById(Serializable id) {
		return super.getById(id);
	}

	@Caching(evict = {
			@CacheEvict(value = "CoreAgency", key = "#entity.id", condition = "#entity.id != null")
	})
	@Override
	public boolean saveOrUpdate(CoreAgency entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "CoreAgency", key = "#entity.id")
	})
	@Override
	public boolean updateById(CoreAgency entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "CoreAgency", allEntries = true)
	})
	@Override
	public boolean update(CoreAgency entity, Wrapper<CoreAgency> updateWrapper) {
		return super.update(entity, updateWrapper);
	}
}
