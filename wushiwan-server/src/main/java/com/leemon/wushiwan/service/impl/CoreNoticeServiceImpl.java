package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CoreBanner;
import com.leemon.wushiwan.entity.CoreNotice;
import com.leemon.wushiwan.mapper.CoreNoticeMapper;
import com.leemon.wushiwan.service.ICoreNoticeService;
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
public class CoreNoticeServiceImpl extends ServiceImpl<CoreNoticeMapper, CoreNotice> implements ICoreNoticeService {

	/**
	 * 返回所有可用的notice
	 *
	 * @return
	 */
	@Override
	public List<CoreNotice> usableList() {
		QueryWrapper<CoreNotice> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreNotice::getUsable, true).orderByAsc(CoreNotice::getSort);
		return list(qw);
	}
}
