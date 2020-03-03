package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CoreBanner;
import com.leemon.wushiwan.mapper.CoreBannerMapper;
import com.leemon.wushiwan.service.ICoreBannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-11-18
 */
@Service
public class CoreBannerServiceImpl extends ServiceImpl<CoreBannerMapper, CoreBanner> implements ICoreBannerService {

	/**
	 * 返回所有可用的banner
	 *
	 * @return
	 */
	@Override
	public List<CoreBanner> usableList() {
		QueryWrapper<CoreBanner> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreBanner::getUsable, true).orderByAsc(CoreBanner::getSort);
		return list(qw);
	}
}
