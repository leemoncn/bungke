package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.entity.CoreMission;
import com.leemon.wushiwan.entity.CoreMissionComplaint;
import com.leemon.wushiwan.mapper.CoreMissionComplaintMapper;
import com.leemon.wushiwan.service.ICoreMissionComplaintService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-06-14
 */
@Service
public class CoreMissionComplaintServiceImpl extends ServiceImpl<CoreMissionComplaintMapper, CoreMissionComplaint> implements ICoreMissionComplaintService {

	@Override
	public CoreMissionComplaint getCoreMissionComplaintByMissionAcceptId(Integer missionAcceptId) {
		QueryWrapper<CoreMissionComplaint> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreMissionComplaint::getMissionAcceptId, missionAcceptId);
		return getOne(qw);
	}
}
