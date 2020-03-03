package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CorePartnerMapper;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class CorePartnerServiceImpl extends ServiceImpl<CorePartnerMapper, CorePartner> implements ICorePartnerService {

	/**
	 * 获取非合作商的ID
	 *
	 * @return
	 */
	@Cacheable(value = "CorePartner", key = "'NoneLevelPartner'")
	@Override
	public CorePartner getNonePartner() {
		QueryWrapper<CorePartner> qw = new QueryWrapper<>();
		qw.lambda().eq(CorePartner::getLevel, 0);
		CorePartner cp = getOne(qw);
		if (cp == null) {
			throw new LogicException(ErrorCode.SYS_ERROR, "请先配置CorePartner表，并添加level为0的非合作商");
		}
		return cp;
	}

	@Cacheable(value = "CorePartner")
	@Override
	public CorePartner getById(Serializable id) {
		return super.getById(id);
	}

	@Caching(evict = {
			@CacheEvict(value = "CorePartner", key = "#entity.id", condition = "#entity.id != null")
	})
	@Override
	public boolean saveOrUpdate(CorePartner entity) {
		return super.saveOrUpdate(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "CorePartner", key = "#entity.id")
	})
	@Override
	public boolean updateById(CorePartner entity) {
		return super.updateById(entity);
	}

	@Caching(evict = {
			@CacheEvict(value = "CorePartner", allEntries = true)
	})
	@Override
	public boolean update(CorePartner entity, Wrapper<CorePartner> updateWrapper) {
		return super.update(entity, updateWrapper);
	}
}
