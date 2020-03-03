package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CoreMissionRule;
import com.leemon.wushiwan.mapper.CoreMissionRuleMapper;
import com.leemon.wushiwan.service.ICoreMissionRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class CoreMissionRuleServiceImpl extends ServiceImpl<CoreMissionRuleMapper, CoreMissionRule> implements ICoreMissionRuleService {

	@Override
	public CoreMissionRule getCoreMissionRuleByMissionTypePropertyId(Integer id) {
		QueryWrapper<CoreMissionRule> qw = new QueryWrapper<>();
		return this.getOne(qw.lambda().eq(CoreMissionRule::getTypePropertyId, id));
	}
}
